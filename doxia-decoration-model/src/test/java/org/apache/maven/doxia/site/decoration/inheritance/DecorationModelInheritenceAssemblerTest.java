package org.apache.maven.doxia.site.decoration.inheritance;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import junit.framework.TestCase;
import org.apache.maven.doxia.site.decoration.Banner;
import org.apache.maven.doxia.site.decoration.Body;
import org.apache.maven.doxia.site.decoration.DecorationModel;
import org.apache.maven.doxia.site.decoration.LinkItem;
import org.apache.maven.doxia.site.decoration.Logo;
import org.apache.maven.doxia.site.decoration.Menu;
import org.apache.maven.doxia.site.decoration.io.xpp3.DecorationXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Test the inheritance assembler.
 *
 * @author <a href="mailto:brett@apache.org">Brett Porter</a>
 */
public class DecorationModelInheritenceAssemblerTest
    extends TestCase
{
    private DecorationModelInheritanceAssembler assembler = new DefaultDecorationModelInheritanceAssembler();

    private static final String NAME = "Name";

    public void testInheritence()
        throws IOException, XmlPullParserException
    {
        DecorationModel childModel = readModel( "child.xml" );
        DecorationModel parentModel = readModel( "parent.xml" );

        assembler.assembleModelInheritance( NAME, childModel, parentModel, "http://maven.apache.org/doxia",
                                            "http://maven.apache.org" );

        DecorationModel mergedModel = readModel( "merged.xml" );
        assertEquals( "Check result", mergedModel, childModel );
    }

    public void testPathsResolvedWhenEmpty()
        throws IOException, XmlPullParserException
    {
        // Test an empty model avoids NPEs
        DecorationModel childModel = readModel( "empty.xml" );
        DecorationModel parentModel = readModel( "empty.xml" );
        assembler.assembleModelInheritance( NAME, childModel, parentModel, "http://maven.apache.org/doxia",
                                            "http://maven.apache.org" );

        DecorationModel mergedModel = readModel( "empty.xml" );
        assertEquals( "Check result", mergedModel, childModel );
    }

    public void testPathsNotResolvedForExternalUrls()
        throws IOException, XmlPullParserException
    {
        DecorationModel parentModel = readModel( "external-urls.xml" );
        DecorationModel childModel = readModel( "empty.xml" );
        assembler.assembleModelInheritance( NAME, childModel, parentModel, "http://maven.apache.org/doxia",
                                            "http://maven.apache.org" );

        assertEquals( "check left banner href", "http://jakarta.apache.org/", childModel.getBannerLeft().getHref() );
        assertEquals( "check left banner image", "http://jakarta.apache.org/images/jakarta-logo.gif",
                      childModel.getBannerLeft().getSrc() );

        assertEquals( "check right banner href", "http://jakarta.apache.org/commons/sandbox",
                      childModel.getBannerRight().getHref() );
        assertEquals( "check right banner image", "http://jakarta.apache.org/commons/images/logo.png",
                      childModel.getBannerRight().getSrc() );

        Logo poweredBy = (Logo) childModel.getPoweredBy().get( 0 );
        assertEquals( "check powered by logo href", "http://tomcat.apache.org/", poweredBy.getHref() );
        assertEquals( "check powered by logo image", "http://tomcat.apache.org/logo.gif", poweredBy.getImg() );

        LinkItem breadcrumb = (LinkItem) childModel.getBody().getBreadcrumbs().get( 0 );
        assertEquals( "check breadcrumb href", "http://www.apache.org/", breadcrumb.getHref() );

        LinkItem link = (LinkItem) childModel.getBody().getLinks().get( 0 );
        assertEquals( "check link href", "http://www.bouncycastle.org", link.getHref() );

        Menu menu = (Menu) childModel.getBody().getMenus().get( 0 );
        LinkItem menuItem = (LinkItem) menu.getItems().get( 0 );
        assertEquals( "check menu item href", "http://www.apache.org/special/", menuItem.getHref() );
    }

    public void testPathsResolvedForRelativeUrls()
        throws IOException, XmlPullParserException
    {
        DecorationModel parentModel = readModel( "relative-urls.xml" );
        DecorationModel childModel = readModel( "empty.xml" );
        assembler.assembleModelInheritance( NAME, childModel, parentModel, "http://maven.apache.org/doxia/",
                                            "http://maven.apache.org" );

        assertEquals( "check left banner href", "../banner/left", childModel.getBannerLeft().getHref() );
        assertEquals( "check left banner image", "../images/jakarta-logo.gif", childModel.getBannerLeft().getSrc() );

        assertEquals( "check right banner href", "../banner/right/", childModel.getBannerRight().getHref() );
        assertEquals( "check right banner image", "../commons/images/logo.png", childModel.getBannerRight().getSrc() );

        Logo poweredBy = (Logo) childModel.getPoweredBy().get( 0 );
        assertEquals( "check powered by logo href", "../tomcat", poweredBy.getHref() );
        assertEquals( "check powered by logo image", "../tomcat/logo.gif", poweredBy.getImg() );

        LinkItem breadcrumb = (LinkItem) childModel.getBody().getBreadcrumbs().get( 0 );
        assertEquals( "check breadcrumb href", "../apache", breadcrumb.getHref() );

        LinkItem link = (LinkItem) childModel.getBody().getLinks().get( 0 );
        assertEquals( "check link href", "../bouncycastle/", link.getHref() );

        Menu menu = (Menu) childModel.getBody().getMenus().get( 0 );
        LinkItem menuItem = (LinkItem) menu.getItems().get( 0 );
        assertEquals( "check menu item href", "../special/", menuItem.getHref() );
    }

    public void testPathsResolvedForSubsiteUrls()
        throws IOException, XmlPullParserException
    {
        DecorationModel parentModel = readModel( "subsite-urls.xml" );
        DecorationModel childModel = readModel( "empty.xml" );
        assembler.assembleModelInheritance( NAME, childModel, parentModel, "http://maven.apache.org/doxia/",
                                            "http://maven.apache.org" );

        assertEquals( "check left banner href", "../banner/left", childModel.getBannerLeft().getHref() );
        assertEquals( "check left banner image", "../images/jakarta-logo.gif", childModel.getBannerLeft().getSrc() );

        assertEquals( "check right banner href", "../banner/right/", childModel.getBannerRight().getHref() );
        assertEquals( "check right banner image", "../commons/images/logo.png", childModel.getBannerRight().getSrc() );

        Logo poweredBy = (Logo) childModel.getPoweredBy().get( 0 );
        assertEquals( "check powered by logo href", "../tomcat", poweredBy.getHref() );
        assertEquals( "check powered by logo image", "../tomcat/logo.gif", poweredBy.getImg() );

        LinkItem breadcrumb = (LinkItem) childModel.getBody().getBreadcrumbs().get( 0 );
        assertEquals( "check breadcrumb href", "../apache", breadcrumb.getHref() );

        LinkItem link = (LinkItem) childModel.getBody().getLinks().get( 0 );
        assertEquals( "check link href", "../bouncycastle/", link.getHref() );

        Menu menu = (Menu) childModel.getBody().getMenus().get( 0 );
        LinkItem menuItem = (LinkItem) menu.getItems().get( 0 );
        assertEquals( "check menu item href", "../special/", menuItem.getHref() );
    }

    public void testPathsResolvedForRelativeUrlsDepthOfTwo()
        throws IOException, XmlPullParserException
    {
        DecorationModel parentModel = readModel( "relative-urls.xml" );
        DecorationModel childModel = readModel( "empty.xml" );
        assembler.assembleModelInheritance( NAME, childModel, parentModel, "http://maven.apache.org/doxia/core",
                                            "http://maven.apache.org" );

        assertEquals( "check left banner href", "../../banner/left", childModel.getBannerLeft().getHref() );
        assertEquals( "check left banner image", "../../images/jakarta-logo.gif", childModel.getBannerLeft().getSrc() );

        assertEquals( "check right banner href", "../../banner/right/", childModel.getBannerRight().getHref() );
        assertEquals( "check right banner image", "../../commons/images/logo.png",
                      childModel.getBannerRight().getSrc() );

        Logo poweredBy = (Logo) childModel.getPoweredBy().get( 0 );
        assertEquals( "check powered by logo href", "../../tomcat", poweredBy.getHref() );
        assertEquals( "check powered by logo image", "../../tomcat/logo.gif", poweredBy.getImg() );

        LinkItem breadcrumb = (LinkItem) childModel.getBody().getBreadcrumbs().get( 0 );
        assertEquals( "check breadcrumb href", "../../apache", breadcrumb.getHref() );

        LinkItem link = (LinkItem) childModel.getBody().getLinks().get( 0 );
        assertEquals( "check link href", "../../bouncycastle/", link.getHref() );

        Menu menu = (Menu) childModel.getBody().getMenus().get( 0 );
        LinkItem menuItem = (LinkItem) menu.getItems().get( 0 );
        assertEquals( "check menu item href", "../../special/", menuItem.getHref() );
    }

    public void testPathsResolvedForReverseRelativeUrls()
        throws IOException, XmlPullParserException
    {
        DecorationModel parentModel = readModel( "relative-urls.xml" );
        DecorationModel childModel = readModel( "empty.xml" );
        assembler.assembleModelInheritance( NAME, childModel, parentModel, "http://maven.apache.org/",
                                            "http://maven.apache.org/doxia/" );

        assertEquals( "check left banner href", "doxia/banner/left", childModel.getBannerLeft().getHref() );
        assertEquals( "check left banner image", "doxia/images/jakarta-logo.gif", childModel.getBannerLeft().getSrc() );

        assertEquals( "check right banner href", "doxia/banner/right/", childModel.getBannerRight().getHref() );
        assertEquals( "check right banner image", "doxia/commons/images/logo.png",
                      childModel.getBannerRight().getSrc() );

        Logo poweredBy = (Logo) childModel.getPoweredBy().get( 0 );
        assertEquals( "check powered by logo href", "doxia/tomcat", poweredBy.getHref() );
        assertEquals( "check powered by logo image", "doxia/tomcat/logo.gif", poweredBy.getImg() );

        LinkItem breadcrumb = (LinkItem) childModel.getBody().getBreadcrumbs().get( 0 );
        assertEquals( "check breadcrumb href", "doxia/apache", breadcrumb.getHref() );

        LinkItem link = (LinkItem) childModel.getBody().getLinks().get( 0 );
        assertEquals( "check link href", "doxia/bouncycastle/", link.getHref() );

        Menu menu = (Menu) childModel.getBody().getMenus().get( 0 );
        LinkItem menuItem = (LinkItem) menu.getItems().get( 0 );
        assertEquals( "check menu item href", "doxia/special/", menuItem.getHref() );
    }

    public void testPathsResolvedForReverseRelativeUrlsDepthOfTwo()
        throws IOException, XmlPullParserException
    {
        DecorationModel parentModel = readModel( "relative-urls.xml" );
        DecorationModel childModel = readModel( "empty.xml" );
        assembler.assembleModelInheritance( NAME, childModel, parentModel, "http://maven.apache.org/",
                                            "http://maven.apache.org/doxia/core/" );

        assertEquals( "check left banner href", "doxia/core/banner/left", childModel.getBannerLeft().getHref() );
        assertEquals( "check left banner image", "doxia/core/images/jakarta-logo.gif",
                      childModel.getBannerLeft().getSrc() );

        assertEquals( "check right banner href", "doxia/core/banner/right/", childModel.getBannerRight().getHref() );
        assertEquals( "check right banner image", "doxia/core/commons/images/logo.png",
                      childModel.getBannerRight().getSrc() );

        Logo poweredBy = (Logo) childModel.getPoweredBy().get( 0 );
        assertEquals( "check powered by logo href", "doxia/core/tomcat", poweredBy.getHref() );
        assertEquals( "check powered by logo image", "doxia/core/tomcat/logo.gif", poweredBy.getImg() );

        LinkItem breadcrumb = (LinkItem) childModel.getBody().getBreadcrumbs().get( 0 );
        assertEquals( "check breadcrumb href", "doxia/core/apache", breadcrumb.getHref() );

        LinkItem link = (LinkItem) childModel.getBody().getLinks().get( 0 );
        assertEquals( "check link href", "doxia/core/bouncycastle/", link.getHref() );

        Menu menu = (Menu) childModel.getBody().getMenus().get( 0 );
        LinkItem menuItem = (LinkItem) menu.getItems().get( 0 );
        assertEquals( "check menu item href", "doxia/core/special/", menuItem.getHref() );
    }

    public void testPathsResolvedForUnrelatedRelativeUrls()
        throws IOException, XmlPullParserException
    {
        DecorationModel parentModel = readModel( "relative-urls.xml" );
        DecorationModel childModel = readModel( "empty.xml" );
        assembler.assembleModelInheritance( NAME, childModel, parentModel, "http://maven.apache.org",
                                            "http://jakarta.apache.org" );

        assertEquals( "check left banner href", "http://jakarta.apache.org/banner/left",
                      childModel.getBannerLeft().getHref() );
        assertEquals( "check left banner image", "http://jakarta.apache.org/images/jakarta-logo.gif",
                      childModel.getBannerLeft().getSrc() );

        assertEquals( "check right banner href", "http://jakarta.apache.org/banner/right/",
                      childModel.getBannerRight().getHref() );
        assertEquals( "check right banner image", "http://jakarta.apache.org/commons/images/logo.png",
                      childModel.getBannerRight().getSrc() );

        Logo poweredBy = (Logo) childModel.getPoweredBy().get( 0 );
        assertEquals( "check powered by logo href", "http://jakarta.apache.org/tomcat", poweredBy.getHref() );
        assertEquals( "check powered by logo image", "http://jakarta.apache.org/tomcat/logo.gif", poweredBy.getImg() );

        LinkItem breadcrumb = (LinkItem) childModel.getBody().getBreadcrumbs().get( 0 );
        assertEquals( "check breadcrumb href", "http://jakarta.apache.org/apache", breadcrumb.getHref() );

        LinkItem link = (LinkItem) childModel.getBody().getLinks().get( 0 );
        assertEquals( "check link href", "http://jakarta.apache.org/bouncycastle/", link.getHref() );

        Menu menu = (Menu) childModel.getBody().getMenus().get( 0 );
        LinkItem menuItem = (LinkItem) menu.getItems().get( 0 );
        assertEquals( "check menu item href", "http://jakarta.apache.org/special/", menuItem.getHref() );
    }

    public void testNullParent()
        throws IOException, XmlPullParserException
    {
        DecorationModel childModel = readModel( "empty.xml" );
        assembler.assembleModelInheritance( NAME, childModel, null, "http://maven.apache.org/doxia",
                                            "http://maven.apache.org" );

        DecorationModel mergedModel = readModel( "empty.xml" );
        assertEquals( "Check result", mergedModel, childModel );
    }

    public void testFullyPopulatedChild()
        throws IOException, XmlPullParserException
    {
        DecorationModel childModel = readModel( "fully-populated-child.xml" );
        DecorationModel parentModel = readModel( "fully-populated-child.xml" );
        assembler.assembleModelInheritance( NAME, childModel, parentModel, "http://foo.apache.org/doxia",
                                            "http://foo.apache.org" );

        DecorationModel mergedModel = readModel( "fully-populated-child.xml" );
        assertEquals( "Check result", mergedModel.toString(), childModel.toString() );
    }

    public void testFullyPopulatedParentAndEmptyChild()
        throws IOException, XmlPullParserException
    {
        DecorationModel childModel = readModel( "empty.xml" );
        DecorationModel parentModel = readModel( "fully-populated-child.xml" );
        assembler.assembleModelInheritance( NAME, childModel, parentModel, "http://maven.apache.org/doxia",
                                            "http://maven.apache.org" );

        DecorationModel mergedModel = readModel( "fully-populated-merged.xml" );
        assertEquals( "Check result", mergedModel, childModel );
    }

    public void testResolvingAllExternalUrls()
        throws IOException, XmlPullParserException
    {
        DecorationModel model = readModel( "external-urls.xml" );

        assembler.resolvePaths( model, "http://foo.com/" );

        DecorationModel resolvedModel = readModel( "external-urls.xml" );
        assertEquals( "Check result", resolvedModel, model );
    }

    public void testResolvingAllRelativeUrls()
        throws IOException, XmlPullParserException
    {
        DecorationModel model = readModel( "relative-urls.xml" );

        assembler.resolvePaths( model, "http://foo.com/" );

        DecorationModel resolvedModel = readModel( "relative-urls-resolved.xml" );
        assertEquals( "Check result", resolvedModel, model );
    }

    public void testResolvingAllSiteUrls()
        throws IOException, XmlPullParserException
    {
        DecorationModel model = readModel( "subsite-urls.xml" );

        assembler.resolvePaths( model, "http://maven.apache.org/" );

        DecorationModel resolvedModel = readModel( "relative-urls-resolved.xml" );
        assertEquals( "Check result", resolvedModel, model );
    }

/* [MSITE-62] This is to test the ../ relative paths, which I am inclined not to use
    public void testResolvingAllSiteChildUrls()
        throws IOException, XmlPullParserException
    {
        DecorationModel model = readModel( "subsite-urls.xml" );

        assembler.resolvePaths( model, "http://maven.apache.org/foo" );

        DecorationModel resolvedModel = readModel( "subsite-relative-urls-resolved.xml" );
        assertEquals( "Check result", resolvedModel, model );
    }

    public void testResolvingAllSiteChildUrlsMultipleLevels()
        throws IOException, XmlPullParserException
    {
        DecorationModel model = readModel( "subsite-urls.xml" );

        assembler.resolvePaths( model, "http://maven.apache.org/banner/right" );

        DecorationModel resolvedModel = readModel( "subsite-relative-urls-multiple-resolved.xml" );
        assertEquals( "Check result", resolvedModel, model );
    }

    public void testResolvingAllSiteChildFilesystemUrls()
        throws IOException, XmlPullParserException
    {
        DecorationModel model = readModel( "subsite-urls-file.xml" );

        assembler.resolvePaths( model, "file://localhost/www/maven.apache.org/foo" );

        DecorationModel resolvedModel = readModel( "subsite-relative-urls-resolved.xml" );
        assertEquals( "Check result", resolvedModel, model );
    }

*/

    public void testResolvingEmptyDescriptor()
        throws IOException, XmlPullParserException
    {
        DecorationModel model = readModel( "empty.xml" );
        assembler.resolvePaths( model, "http://maven.apache.org" );

        DecorationModel resolvedModel = readModel( "empty.xml" );
        assertEquals( "Check result", resolvedModel, model );
    }

    public void testDuplicateParentElements()
    {
        DecorationModel model = new DecorationModel();
        model.setBody( new Body() );
        model.getBody().addLink( createLinkItem( "Foo", "http://foo.apache.org" ) );
        model.getBody().addLink( createLinkItem( "Foo", "http://foo.apache.org" ) );

        model.addPoweredBy( createLogo( "Foo", "http://foo.apache.org", "http://foo.apache.org/foo.jpg" ) );
        model.addPoweredBy( createLogo( "Foo", "http://foo.apache.org", "http://foo.apache.org/foo.jpg" ) );

        DecorationModel child = new DecorationModel();
        assembler.assembleModelInheritance( NAME, child, model, "http://maven.apache.org/doxia",
                                            "http://maven.apache.org" );

        assertEquals( "Check size", 1, child.getBody().getLinks().size() );
        assertEquals( "Check item", createLinkItem( "Foo", "http://foo.apache.org" ),
                      child.getBody().getLinks().get( 0 ) );

        assertEquals( "Check size", 1, child.getPoweredBy().size() );
        assertEquals( "Check item", createLogo( "Foo", "http://foo.apache.org", "http://foo.apache.org/foo.jpg" ),
                      child.getPoweredBy().get( 0 ) );
    }

    public void testDuplicateChildElements()
    {
        DecorationModel model = new DecorationModel();
        model.setBody( new Body() );
        model.getBody().addLink( createLinkItem( "Foo", "http://foo.apache.org" ) );
        model.getBody().addLink( createLinkItem( "Foo", "http://foo.apache.org" ) );

        model.addPoweredBy( createLogo( "Foo", "http://foo.apache.org", "http://foo.apache.org/foo.jpg" ) );
        model.addPoweredBy( createLogo( "Foo", "http://foo.apache.org", "http://foo.apache.org/foo.jpg" ) );

        DecorationModel parent = new DecorationModel();
        assembler.assembleModelInheritance( NAME, model, parent, "http://maven.apache.org/doxia",
                                            "http://maven.apache.org" );

        assertEquals( "Check size", 1, model.getBody().getLinks().size() );
        assertEquals( "Check item", createLinkItem( "Foo", "http://foo.apache.org" ),
                      model.getBody().getLinks().get( 0 ) );

        assertEquals( "Check size", 1, model.getPoweredBy().size() );
        assertEquals( "Check item", createLogo( "Foo", "http://foo.apache.org", "http://foo.apache.org/foo.jpg" ),
                      model.getPoweredBy().get( 0 ) );
    }

    public void testBreadcrumbWithoutHref()
    {
        DecorationModel model = new DecorationModel();
        model.setBody( new Body() );
        model.getBody().addBreadcrumb( createLinkItem( "Foo", null ) );
        assembler.resolvePaths( model, "http://foo.apache.org" );
        assertEquals( "Check size", 1, model.getBody().getBreadcrumbs().size() );
        assertEquals( "Check item", createLinkItem( "Foo", "" ), model.getBody().getBreadcrumbs().get( 0 ) );
    }

    public void testBannerWithoutHref()
    {
        DecorationModel model = new DecorationModel();
        model.setBody( new Body() );

        Banner banner = createBanner( "Left", null, "/images/src.gif", "alt" );

        model.setBannerLeft( banner );

        assembler.resolvePaths( model, "http://foo.apache.org" );

        assertEquals( "Check banner", createBanner( "Left", null, "images/src.gif", "alt" ), model.getBannerLeft() );
    }

    public void testLogoWithoutImage()
    {
        // This should actually be validated in the model, it doesn't really make sense
        DecorationModel model = new DecorationModel();
        model.setBody( new Body() );
        model.addPoweredBy( createLogo( "Foo", "http://foo.apache.org", null ) );
        assembler.resolvePaths( model, "http://foo.apache.org" );
        assertEquals( "Check size", 1, model.getPoweredBy().size() );
        assertEquals( "Check item", createLogo( "Foo", "http://foo.apache.org", null ), model.getPoweredBy().get( 0 ) );
    }

    private static Banner createBanner( String name, String href, String src, String alt )
    {
        Banner banner = new Banner();
        banner.setName( name );
        banner.setHref( href );
        banner.setSrc( src );
        banner.setAlt( alt );
        return banner;
    }

    private Logo createLogo( String name, String href, String img )
    {
        Logo logo = new Logo();
        logo.setHref( href );
        logo.setImg( img );
        logo.setName( name );
        return logo;
    }

    private static LinkItem createLinkItem( String name, String href )
    {
        LinkItem item = new LinkItem();
        item.setName( name );
        item.setHref( href );
        return item;
    }

    private DecorationModel readModel( String name )
        throws IOException, XmlPullParserException
    {
        return new DecorationXpp3Reader().read( new InputStreamReader( getClass().getResourceAsStream( "/" + name ) ) );
    }
}

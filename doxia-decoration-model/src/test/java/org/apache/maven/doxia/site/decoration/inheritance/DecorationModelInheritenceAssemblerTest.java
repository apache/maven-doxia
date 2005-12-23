package org.apache.maven.doxia.site.decoration.inheritance;

/*
 * Copyright 2004-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import junit.framework.TestCase;
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

    public void testInheritence()
        throws IOException, XmlPullParserException
    {
        DecorationModel childModel = readModel( "child.xml" );
        DecorationModel parentModel = readModel( "parent.xml" );

        assembler.assembleModelInheritance( childModel, parentModel, "http://maven.apache.org/doxia",
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
        assembler.assembleModelInheritance( childModel, parentModel, "http://maven.apache.org/doxia",
                                            "http://maven.apache.org" );

        DecorationModel mergedModel = readModel( "empty.xml" );
        assertEquals( "Check result", mergedModel, childModel );
    }

    public void testPathsNotResolvedForExternalUrls()
        throws IOException, XmlPullParserException
    {
        DecorationModel parentModel = readModel( "external-urls.xml" );
        DecorationModel childModel = readModel( "empty.xml" );
        assembler.assembleModelInheritance( childModel, parentModel, "http://maven.apache.org/doxia",
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
        assembler.assembleModelInheritance( childModel, parentModel, "http://maven.apache.org/doxia/",
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
        assembler.assembleModelInheritance( childModel, parentModel, "http://maven.apache.org/doxia/",
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
        assembler.assembleModelInheritance( childModel, parentModel, "http://maven.apache.org/doxia/core",
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
        assembler.assembleModelInheritance( childModel, parentModel, "http://maven.apache.org/",
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
        assembler.assembleModelInheritance( childModel, parentModel, "http://maven.apache.org/",
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
        assembler.assembleModelInheritance( childModel, parentModel, "http://maven.apache.org",
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
        assembler.assembleModelInheritance( childModel, null, "http://maven.apache.org/doxia",
                                            "http://maven.apache.org" );

        DecorationModel mergedModel = readModel( "empty.xml" );
        assertEquals( "Check result", mergedModel, childModel );
    }

    public void testFullyPopulatedChild()
        throws IOException, XmlPullParserException
    {
        DecorationModel childModel = readModel( "fully-populated-child.xml" );
        DecorationModel parentModel = readModel( "fully-populated-child.xml" );
        assembler.assembleModelInheritance( childModel, parentModel, "http://maven.apache.org/doxia",
                                            "http://maven.apache.org" );

        DecorationModel mergedModel = readModel( "fully-populated-child.xml" );
        assertEquals( "Check result", mergedModel, childModel );
    }

    public void testFullyPopulatedParentAndEmptyChild()
        throws IOException, XmlPullParserException
    {
        DecorationModel childModel = readModel( "empty.xml" );
        DecorationModel parentModel = readModel( "fully-populated-child.xml" );
        assembler.assembleModelInheritance( childModel, parentModel, "http://maven.apache.org/doxia",
                                            "http://maven.apache.org" );

        DecorationModel mergedModel = readModel( "fully-populated-merged.xml" );
        assertEquals( "Check result", mergedModel, childModel );
    }

    private DecorationModel readModel( String name )
        throws IOException, XmlPullParserException
    {
        return new DecorationXpp3Reader().read( new InputStreamReader( getClass().getResourceAsStream( "/" + name ) ) );
    }
}

<?xml version="1.0" encoding="UTF-8"?>

<!--
    /*
    * Copyright 2001-2006 The Apache Software Foundation.
    *
    * Licensed under the Apache License, Version 2.0 (the "License");
    * you may not use this file except in compliance with the License.
    * You may obtain a copy of the License at
    *
    *      http://www.apache.org/licenses/LICENSE-2.0
    *
    * Unless required by applicable law or agreed to in writing, software
    * distributed under the License is distributed on an "AS IS" BASIS,
    * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    * See the License for the specific language governing permissions and
    * limitations under the License.
    */
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <!--  MetaData Parameters -->
    <xsl:param name="title" />
    <xsl:param name="author" />
    <xsl:param name="creationdate" />
    <xsl:param name="subject" />
    <xsl:param name="keywords" />
    <xsl:param name="producer" />
    <xsl:param name="pagesize" />

    <!--  FrontPage Parameters -->
    <xsl:param name="frontPageTitle" />
    <xsl:param name="frontPageFooter" />

    <xsl:template match="/itext">
        <itext title="{$title}" author="{$author}" creationdate="{$creationdate}"
            subject="{$subject}" keywords="{$keywords}" producer="{$producer}"
            pagesize="{$pagesize}">
        <!-- Start Front page -->
        <paragraph>
            <chunk font="Helvetica" size="12.0" fontstyle="normal"
                blue="0" green="0" red="0">
                <table columns="1" left="false" right="false"
                    top="false" bottom="false" align="Center" width="100%">
                    <row>
                        <cell left="false" right="false" top="false"
                            bottom="false" horizontalalign="Left" verticalalign="middle">
                            <image
                                url="http://maven.apache.org/images/apache-maven-project.png"
                                plainwidth="306.0" plainheight="27.867857" />
                        </cell>
                    </row>
                    <row>
                        <cell left="false" right="false" top="false"
                            bottom="false" horizontalalign="Center" verticalalign="middle"
                            leading="330">
                            <chunk font="Helvetica" size="24.0"
                                fontstyle="bold" blue="0" green="0" red="0"><xsl:value-of select="$frontPageTitle"/></chunk>
                        </cell>
                    </row>
                    <row>
                        <cell left="false" right="false" top="false"
                            bottom="false" horizontalalign="Left" verticalalign="middle"
                            leading="330">
                            <chunk font="Helvetica" size="16.0"
                                fontstyle="bold" blue="0" green="0" red="0"><xsl:value-of select="$frontPageFooter"/></chunk>
                        </cell>
                    </row>
                </table>
            </chunk>
        </paragraph>
        <!-- End Front page -->

        <!-- Start TOC -->
        <newpage />
        <paragraph align="Center">
            <chunk font="Helvetica" size="24" fontstyle="bold" blue="0"
                green="0" red="0">Table Of Contents</chunk>
        </paragraph>
        <paragraph align="Left" leading="24.0">
            <newline />
            <xsl:apply-templates select="*" mode="toc" />
        </paragraph>
        <!-- End TOC -->

        <xsl:apply-templates select="*" mode="body" />

        </itext>
    </xsl:template>

    <!-- Add TOC -->
    <xsl:template match="chapter|section" mode="toc">
        <xsl:if test="./title/chunk != ''">
            <chunk font="Helvetica" size="16.0" fontstyle="normal"
                blue="255" green="0" red="0"
                localgoto="{generate-id(./title/chunk)}">
                <xsl:number level="multiple" format="1.1.1.1.1."
                    count="section|chapter" />
                <xsl:text> </xsl:text>
                <xsl:value-of select="title/chunk" />
            </chunk>
        </xsl:if>
        <xsl:if test="./title/anchor != ''">
            <xsl:if test="./title/anchor/@name != ''">
                <chunk font="Helvetica" size="16.0" fontstyle="normal"
                    blue="255" green="0" red="0" localgoto="{./title/anchor/@name}">
                    <xsl:number level="multiple" format="1.1.1.1.1."
                        count="section|chapter" />
                    <xsl:text> </xsl:text>
                    <xsl:value-of select="title/anchor/chunk" />
                </chunk>
            </xsl:if>
            <xsl:if test="./title/anchor/@name = ''">
                <chunk font="Helvetica" size="16.0" fontstyle="normal"
                    blue="0" green="0" red="0">
                    <xsl:number level="multiple" format="1.1.1.1.1."
                        count="section|chapter" />
                    <xsl:text> </xsl:text>
                    <xsl:value-of select="title/anchor/chunk" />
                </chunk>
            </xsl:if>
        </xsl:if>

        <newline />
        <xsl:apply-templates select="child::*[name() = 'section']"
            mode="toc" />
    </xsl:template>

    <xsl:template match="chapter/title/chunk|section/title/chunk"
        mode="body">
        <xsl:copy>
            <xsl:apply-templates select="@*" mode="body" />
            <xsl:attribute name="localdestination">
                <xsl:value-of select="generate-id(.)" />
            </xsl:attribute>
            <xsl:apply-templates select="text()|*" mode="body" />
        </xsl:copy>
    </xsl:template>

    <xsl:template
        match="chapter/title/anchor/chunk|section/title/anchor/chunk"
        mode="body">
        <xsl:copy>
            <xsl:apply-templates select="@*" mode="body" />
            <xsl:apply-templates select="text()|*" mode="body" />
        </xsl:copy>
    </xsl:template>

    <xsl:template match="*|text()|@*" mode="body">
        <xsl:copy>
            <xsl:apply-templates select="*|text()|@*" mode="body" />
        </xsl:copy>
    </xsl:template>

    <!--  Update depth and numberdepth -->
    <xsl:template match="chapter" mode="body">
        <xsl:copy>
            <xsl:apply-templates select="@*" mode="body" />
            <xsl:attribute name="depth">1</xsl:attribute>
            <xsl:attribute name="numberdepth">1</xsl:attribute>
            <xsl:apply-templates select="text()|*" mode="body" />
        </xsl:copy>
    </xsl:template>

    <xsl:template match="section" mode="body">
        <xsl:copy>
            <xsl:apply-templates select="@*" mode="body" />
            <xsl:variable name="depth.from.context"
                select="count(ancestor::*)" />
            <xsl:attribute name="depth">
                <xsl:value-of select="$depth.from.context" />
            </xsl:attribute>
            <xsl:attribute name="numberdepth">
                <xsl:value-of select="$depth.from.context" />
            </xsl:attribute>
            <xsl:apply-templates select="text()|*" mode="body" />
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>

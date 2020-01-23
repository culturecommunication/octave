<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:fo="http://www.w3.org/1999/XSL/Format" 
	xmlns:EAD="urn:isbn:1-931666-22-9"
	xmlns:xlink="http://www.w3.org/1999/xlink" 
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
	xmlns:f="Functions" 
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:LEVELS="http://www.docuteam.ch/xmlns/levels"
	xsi:schemaLocation="   urn:isbn:1-931666-22-9 http://www.loc.gov/ead/ead.xsd   http://www.w3.org/1999/xlink http://www.loc.gov/standards/mets/xlink.xsd">
	
	<xsl:output method="xml" encoding="UTF-8" indent="yes" />
	
	<!-- The solution for parsing text files in a properties-like structure 
		was originally taken from http://stackoverflow.com/a/4326531 -->
	
	<!-- Selecting the correct value from the translation properties file has to take into account some situations:
		1. The key could also appear as the value, 
		in which case we get an even count of hits and need not add 1 to the looked-for index
		2. A key could appear several times if the generic translations are kept in the upper part of the file,
		and modified translations appended at the end; that's why the 'max' function is used 
		to get the last and presumably intended value.
	-->
	<xsl:variable name="translations" select="unparsed-text('../../translations/Translations.properties', 'ISO-8859-1')" as="xs:string" />
	<xsl:variable name="lines_translations" as="xs:string*"
		select="
		for $x in 
			for $i in tokenize($translations, '\n')[matches(., '^[^!#]')] return
				tokenize($i, '=')
			return translate(normalize-space($x), '\', '')" />
	<xsl:function name="f:getTranslation" as="xs:string?">
		<xsl:param name="key" as="xs:string" />
		<!-- <xsl:variable name="hits" select="count(index-of($lines_translations, $key))" /> -->
		<xsl:choose>
			<!-- <xsl:when test="$hits mod 2 = 1"> -->
			<xsl:when test="$lines_translations[max(index-of($lines_translations, $key))] = $lines_translations[max(index-of($lines_translations, $key))-1]">
				<xsl:sequence select="$lines_translations[max(index-of($lines_translations, $key))]" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="$lines_translations[max(index-of($lines_translations, $key))+1]" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	
	<!-- the second call of 'tokenize' has to include spaces before/after the equals sign
		as this character as such also appears in keys and values. -->
	<xsl:variable name="accessors" select="unparsed-text('EAD2Accessor.properties', 'UTF-8')" as="xs:string" />
	<xsl:variable name="lines_accessors" as="xs:string*"
		select="
		for $x in 
			for $i in tokenize($accessors, '\n')[matches(., '^[^!#]')] return
				tokenize($i, ' = ')
			return translate(normalize-space($x), '\', '')" />
	<xsl:function name="f:getAccessor" as="xs:string?">
		<xsl:param name="key" as="xs:string" />
		<xsl:sequence select="$lines_accessors[index-of($lines_accessors, $key)+1]" />
	</xsl:function>
	
	<xsl:template match="/">
		<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" font-family="Arial">
			<fo:layout-master-set>
				<fo:simple-page-master master-name="Deckblatt" margin-top="1.5cm" margin-bottom="1cm" margin-left="1.5cm" margin-right="1cm">
					<fo:region-body region-name="body" margin-top="2cm" margin-bottom="1cm"/>
					<fo:region-before region-name="header" extent="2cm" />
					<fo:region-after region-name="footer" extent=".5cm" />
				</fo:simple-page-master>
				<fo:simple-page-master master-name="Folgeseite" margin-top="1.5cm" margin-bottom="1cm" margin-left="1.5cm" margin-right="1cm">
					<fo:region-body region-name="body" margin-top="2cm" margin-bottom="1cm"/>
					<fo:region-before region-name="header" extent="2cm" />
					<fo:region-after region-name="footer" extent=".5cm" />
				</fo:simple-page-master>
				<fo:page-sequence-master master-name="Bericht">
					<fo:single-page-master-reference master-reference="Deckblatt" />
					<fo:repeatable-page-master-reference master-reference="Folgeseite" />
				</fo:page-sequence-master>
			</fo:layout-master-set>
			<fo:page-sequence master-reference="Bericht" id="report">
				<fo:static-content flow-name="header" font-size="8pt">
					<fo:table>
						<fo:table-column width="50%" />
						<fo:table-column width="50%" />
						<fo:table-body>
							<!-- TODO: translate labels -->
							<fo:table-row border-after-style="solid">
								<fo:table-cell text-align="left">
									<fo:block font-weight="bold" font-size="14pt" space-after="18pt">
										Metadatenübersicht
									</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="right">
									    <xsl:element name="fo:external-graphic">
									        <xsl:attribute name="src" select="resolve-uri('../../images/Logo_docuteam_64.png')" />
									    </xsl:element>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
						</fo:table-body>
					</fo:table>
				</fo:static-content>
				<fo:static-content flow-name="footer" font-size="8pt">
					<fo:table>
						<fo:table-column width="50%" />
						<fo:table-column width="50%" />
						<fo:table-body>
							<!-- TODO: translate labels -->
							<fo:table-row border-before-style="solid">
								<fo:table-cell text-align="left">
									<fo:block>
										<xsl:value-of select="concat('erstellt am: ', format-date(current-date(), '[D01].[M01].[Y0001]'))" />
									</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="right">
										<xsl:text>Seite&#160;</xsl:text>
										<fo:page-number/>
										<xsl:text>&#160;von&#160;</xsl:text> 
										<fo:page-number-citation-last ref-id="report"/>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
						</fo:table-body>
					</fo:table>
				</fo:static-content>
				<fo:flow flow-name="body" font-size="10pt" page-break-before="always">
					<fo:block-container>
						<xsl:for-each select="//EAD:archdesc">
							<xsl:call-template name="archdesc_component" />
						</xsl:for-each>
					</fo:block-container>
				</fo:flow>
			</fo:page-sequence>
		</fo:root>
	</xsl:template>
	
	<xsl:template name="archdesc_component">
		<xsl:param name="level" select="'0'" />
		<xsl:variable name="indentation" select="number($level)*25" />
		<fo:block-container>
			<fo:block space-before="18pt">
				<fo:table>
					<xsl:attribute name="start-indent" select="$indentation" />
					<fo:table-column column-width="4cm" />
					<fo:table-column column-width="1cm" />
					<fo:table-column />
					<fo:table-body start-indent="0">
						<xsl:call-template name="title">
							<xsl:with-param name="level" select="$level" />
						</xsl:call-template>
						<xsl:call-template name="metadata">
							<xsl:with-param name="level" select="$level" />
						</xsl:call-template>
					</fo:table-body>
				</fo:table>
			</fo:block>
			<xsl:for-each select="EAD:dsc/EAD:c | EAD:c">
				<xsl:call-template name="archdesc_component">
					<xsl:with-param name="level" select="number($level) + 1" />
				</xsl:call-template>
			</xsl:for-each>
		</fo:block-container>
	</xsl:template>
	
	<xsl:template name="title">
		<xsl:param name="level" select="'0'" />
		<fo:table-row font-weight="bold" display-align="center">
			<fo:table-cell>
				<fo:block padding-before="1" padding-after="1">
					<xsl:call-template name="level" />
				</fo:block>
			</fo:table-cell>
			<fo:table-cell>
				<fo:block />
			</fo:table-cell>
			<fo:table-cell>
				<fo:block padding-before="1" padding-after="1">
					<xsl:value-of select="EAD:did/EAD:unittitle[@label='main']" />
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
	</xsl:template>
	
	<xsl:template name="level">
		<xsl:variable name="currentlevel" select="@otherlevel" />
		<xsl:variable name="image" select="document('../../config/levels.xml', document(''))//LEVELS:Level[@nameID=$currentlevel]/@iconFileName" />
		<fo:table>
			<fo:table-column column-width="16px"/>
			<fo:table-column column-width="3pt"/>
			<fo:table-column/>
			<fo:table-body display-align="center">
				<fo:table-row>
					<fo:table-cell>
						<fo:block>
							<!-- for debugging: <xsl:value-of select="$image" /> -->
							<xsl:element name="fo:external-graphic">
								<xsl:attribute name="content-height">scale-to-fit</xsl:attribute>
								<xsl:attribute name="height">16px</xsl:attribute>
								<xsl:attribute name="width">16px</xsl:attribute>
								<xsl:attribute name="src" select="resolve-uri(concat('../../', $image))" />
							</xsl:element>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block />
					</fo:table-cell>
					<fo:table-cell>
						<fo:block>
							<xsl:value-of select="@otherlevel" />
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
	</xsl:template>
	
	<xsl:template name="metadata">
		<xsl:param name="level" select="'0'" />
		<xsl:for-each
			select="EAD:*[not(name(.)='EAD:dsc' or name(.)='EAD:did' or name(.)='EAD:controlaccess' or name(.)='EAD:c')]|EAD:did/*[not(name(.)='EAD:unittitle' and @label='main')]|EAD:controlaccess/*">
			<fo:table-row>
				<fo:table-cell>
					<fo:block padding-before="1" padding-after="1">
						<xsl:choose>
							<xsl:when test="count(@*)=0">
								<xsl:variable name="accessorName" select="f:getAccessor(name(.))" />
								<xsl:choose>
									<xsl:when test="$accessorName!=''">
										<xsl:variable name="accessorNameTranslated" select="f:getTranslation($accessorName)" />
										<xsl:choose>
											<xsl:when test="$accessorNameTranslated!=''">
												<xsl:value-of select="$accessorNameTranslated" />
											</xsl:when>
											<xsl:otherwise>
												<xsl:value-of select="concat('[', $accessorName, ']')" />
											</xsl:otherwise>
										</xsl:choose>
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="concat('{', replace(name(.), 'EAD:', ''), '}')" />
									</xsl:otherwise>
								</xsl:choose>
							</xsl:when>
							<xsl:otherwise>
								<xsl:for-each select="@*[not(name(.)='xlink:href')]">
									<xsl:variable name="accessorName" select="f:getAccessor(concat(name(..), '[@', name(.), '=''', ., ''']'))" />
									<xsl:choose>
										<xsl:when test="$accessorName!=''">
											<xsl:variable name="accessorNameTranslated" select="f:getTranslation($accessorName)" />
											<xsl:choose>
												<xsl:when test="$accessorNameTranslated!=''">
													<xsl:value-of select="$accessorNameTranslated" />
												</xsl:when>
												<xsl:otherwise>
													<xsl:value-of select="concat('[', $accessorName, ']')" />
												</xsl:otherwise>
											</xsl:choose>
										</xsl:when>
									</xsl:choose>
								</xsl:for-each>
							</xsl:otherwise>
						</xsl:choose>
					</fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block />
				</fo:table-cell>
				<fo:table-cell>
					<fo:block padding-before="1" padding-after="1">
						<xsl:choose>
							<xsl:when test="name(.)='EAD:dao'">
								<xsl:value-of select="@xlink:href" />
							</xsl:when>
							<xsl:otherwise>
								<xsl:call-template name="itemizeBySemicolon" />
							</xsl:otherwise>
						</xsl:choose>
					</fo:block>
				</fo:table-cell>
			</fo:table-row>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template name="itemizeBySemicolon">
		<xsl:choose>
			<xsl:when test="matches(., ';')">
				<xsl:for-each select="tokenize(., ';')">
					<fo:block>
						<xsl:text>-&#160;</xsl:text>
						<xsl:value-of select="." />
					</fo:block>
				</xsl:for-each>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="." />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>

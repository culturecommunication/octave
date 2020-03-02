<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:METS="http://www.loc.gov/METS/"
	xmlns:PREMIS="info:lc/xmlns/premis-v2"
	xmlns:EAD="urn:isbn:1-931666-22-9"
	xmlns:xlink="http://www.w3.org/1999/xlink" 
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
	xmlns:f="Functions" 
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:functx="http://www.functx.com"
	xsi:schemaLocation="   urn:isbn:1-931666-22-9 http://www.loc.gov/ead/ead.xsd   http://www.w3.org/1999/xlink http://www.loc.gov/standards/mets/xlink.xsd">
	
	<xsl:output method="xml" encoding="UTF-8" indent="yes" />
	<xsl:decimal-format name="european" decimal-separator="," grouping-separator="'" />
	
	<xsl:variable name="submission-agreement" select="document('../../agreements/sa_all-formats-01.xml')"/>
	<xsl:variable name="producer-name" select="$submission-agreement//producer//departement" />
	<xsl:variable name="producer-contact" select="$submission-agreement//producer/contact/@contactID" />
	<xsl:variable name="archive-name" select="$submission-agreement//archive//departement" />
	<xsl:variable name="archive-contact" select="$submission-agreement//archive/contact/@contactID" />

	<xsl:variable name="number-of-files-restitution">
		<xsl:variable name="files-restitution">
			<xsl:for-each select="//METS:div[@TYPE = 'file']">
				<xsl:variable name="dmd-id" select=".//METS:div[@LABEL = 'EAD']/@DMDID"/>
				<xsl:if test="//METS:dmdSec[@ID = $dmd-id]//EAD:c/@otherlevel = 'Restitution'">
					<file/>
				</xsl:if>
			</xsl:for-each>
		</xsl:variable>
		<xsl:value-of select="count($files-restitution/file)"/>
	</xsl:variable>
	
	<xsl:variable name="number-of-folders-restitution">
		<xsl:variable name="folders-restitution">
			<xsl:for-each select="//METS:div[@TYPE = 'folder']">
				<xsl:variable name="dmd-id" select="./METS:div[@LABEL = 'EAD']/@DMDID"/>
				<xsl:if test="//METS:dmdSec[@ID = $dmd-id]//EAD:c/@otherlevel = 'Restitution'">
					<folder/>
				</xsl:if>
			</xsl:for-each>
		</xsl:variable>
		<xsl:value-of select="count($folders-restitution/folder)"/>
	</xsl:variable>
	
	<xsl:variable name="total-size">
		<xsl:value-of select="xs:decimal(sum(//METS:digiprovMD//PREMIS:size))"/>
	</xsl:variable>
	
	<xsl:variable name="size-of-all-files-restitution">
		<xsl:variable name="files-restitution">
			<xsl:for-each select="//METS:div[@TYPE = 'file']">
				<xsl:variable name="dmd-id" select=".//METS:div[@LABEL = 'EAD']/@DMDID"/>
				<xsl:if test="//METS:dmdSec[@ID = $dmd-id]//EAD:c/@otherlevel = 'Restitution'">
					<xsl:variable name="adm-id" select="./@ADMID"/>
					<xsl:variable name="file-size" select="//METS:digiprovMD[@ID = $adm-id]//PREMIS:size" as="xs:double" />
					<file-size><xsl:value-of select="//METS:digiprovMD[@ID = $adm-id]//PREMIS:size"/></file-size>
				</xsl:if>
			</xsl:for-each>
		</xsl:variable>
		<xsl:value-of select="sum($files-restitution/file-size)"/>
	</xsl:variable>
	
	<xsl:variable name="sorted-dates">
		<xsl:perform-sort select="//EAD:c[@otherlevel = 'Restitution']//EAD:unitdate[@label = 'to' or @label = 'from']">
			<xsl:sort select="." order="ascending"/>
		</xsl:perform-sort>
	</xsl:variable>
	
	<xsl:variable name="date-from">
		<xsl:variable name="date" select="$sorted-dates/EAD:unitdate[1]"/>
		<xsl:choose>
			<xsl:when test="contains($date, '-00-00')" >
				<xsl:value-of select="substring-before($date, '-00-00')"/>
			</xsl:when>
			<xsl:when test="contains($date, '-00')" >
				<xsl:value-of select="substring-before($date, '-00')"/>
			</xsl:when>
			<xsl:when test="not(contains($date, '-00'))" >
				<xsl:value-of select="format-date($date, '[D01].[M01].[Y0001]')" />
			</xsl:when>
		</xsl:choose>
	</xsl:variable>
	
	<xsl:variable name="date-to">
		<xsl:variable name="date" select="$sorted-dates/EAD:unitdate[last()]"/>
		<xsl:choose>
			<xsl:when test="contains($date, '-00-00')" >
				<xsl:value-of select="substring-before($date, '-00-00')"/>
			</xsl:when>
			<xsl:when test="contains($date, '-00')" >
				<xsl:value-of select="substring-before($date, '-00')"/>
			</xsl:when>
			<xsl:when test="not(contains($date, '-00') and contains($date, '-00-00'))" >
				<xsl:value-of select="format-date($date, '[D01].[M01].[Y0001]')" />
			</xsl:when>
		</xsl:choose>
	</xsl:variable>
	
	<xsl:variable name="date-today">
		<xsl:value-of select="format-date(current-date(), '[D01].[M01].[Y0001]')" />
	</xsl:variable>
	
	<xsl:template match="/">
		<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" font-family="Arial">
			
			<fo:layout-master-set>
				<fo:simple-page-master master-name="front-page" margin-top="1.5cm" margin-bottom="1cm" margin-left="1.5cm" margin-right="1cm">
					<fo:region-body region-name="body" margin-top="2cm" margin-bottom="1cm"/>
					<fo:region-after region-name="footer" extent=".5cm" />
				</fo:simple-page-master>
				<fo:simple-page-master master-name="following-page" margin-top="1.5cm" margin-bottom="1cm" margin-left="1.5cm" margin-right="1cm" reference-orientation="90">
					<fo:region-body region-name="body" margin-top="2cm" margin-bottom="1cm"/>
					<fo:region-after region-name="footer" extent=".5cm" />
				</fo:simple-page-master>
				<fo:page-sequence-master master-name="Bericht">
					<fo:single-page-master-reference master-reference="front-page" />
					<fo:repeatable-page-master-reference master-reference="following-page" />
				</fo:page-sequence-master>
			</fo:layout-master-set>
			
			<fo:page-sequence master-reference="Bericht" id="report">
				<fo:static-content flow-name="footer" font-size="8pt">
					<fo:table>
						<fo:table-column width="50%" />
						<fo:table-column width="50%" />
						<fo:table-body>
							<fo:table-row border-before-style="solid">
								<fo:table-cell text-align="left">
									<fo:block>
										<xsl:text>Crée le : </xsl:text><xsl:value-of select="$date-today" />
									</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="right">
										<xsl:text>Page&#160;</xsl:text>
										<fo:page-number/>
										<xsl:text>&#160;de&#160;</xsl:text> 
										<fo:page-number-citation-last ref-id="report"/>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
						</fo:table-body>
					</fo:table>
				</fo:static-content>
				<fo:flow flow-name="body" font-size="10pt">
					<fo:block-container>
						<fo:block font-size="15pt" font-weight="bold" text-align="center" space-after="15pt">
							<xsl:text>Bordereau de restitution d'archives</xsl:text>
						</fo:block>
						<fo:block font-size="10pt" font-style="italic" text-align="center" space-after="15pt">
							<xsl:text>à imprimer et viser en deux exemplaires, un pour le service d'archives, un pour le service producteur</xsl:text>
						</fo:block>
						<fo:block font-size="10pt" space-after="15pt" >
							<xsl:text>N° d'enregistrement du service d'archives :</xsl:text>
						</fo:block>
					</fo:block-container>
					<fo:block-container>
						<fo:block>
							<fo:table border="solid" text-align="left">
								<fo:table-column width="50%" border="solid"/>
								<fo:table-column width="50%" border="solid"/>
								<fo:table-body >
									<fo:table-row font-weight="bold">
										<fo:table-cell padding="5pt">
											<fo:block>
												<xsl:text>Service producteur</xsl:text>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell padding="5pt">
											<fo:block>
												<xsl:text>Service d'archives</xsl:text>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
									<fo:table-row>
										<fo:table-cell padding="5pt">
											<fo:block>
												<xsl:text>Nom : </xsl:text><xsl:value-of select="$producer-name"/>
											</fo:block>
											<fo:block>
												<xsl:text>Adresse : </xsl:text>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell padding="5pt">
											<fo:block>
												<xsl:text>Nom : </xsl:text><xsl:value-of select="$archive-name"/>
											</fo:block>
											<fo:block>
												<xsl:text>Adresse : </xsl:text>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
									<fo:table-row>
										<fo:table-cell padding="5pt">
											<fo:block>
												<xsl:text>Correspondant :</xsl:text>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell padding="5pt">
											<fo:block>
												<xsl:text>Affaire suivie par : </xsl:text>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
									<fo:table-row border-after-style="solid">
										<fo:table-cell padding="5pt">
											<fo:block>
												<xsl:text>Nom : </xsl:text><xsl:value-of select="$producer-contact" /> 
											</fo:block>
											<fo:block>
												<xsl:text>Téléphone : </xsl:text>
											</fo:block>
											<fo:block>
												<xsl:text>Adresse mail : </xsl:text>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell padding="5pt">
											<fo:block>
												<xsl:text>Nom : </xsl:text><xsl:value-of select="$archive-contact" /> 
											</fo:block>
											<fo:block>
												<xsl:text>Téléphone : </xsl:text>
											</fo:block>
											<fo:block>
												<xsl:text>Adresse mail : </xsl:text>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
									<fo:table-row border-after-style="solid">
										<fo:table-cell number-columns-spanned="2" padding="5pt">
											<fo:block>
												<xsl:text>Nombre total d'articles (fichiers, dossiers) dont la restitution est proposée : </xsl:text>
											</fo:block>
											<fo:block>
												<xsl:text>Fichiers : </xsl:text><xsl:value-of select="$number-of-files-restitution"/>
											</fo:block>
											<fo:block>
												<xsl:text>Dossiers : </xsl:text><xsl:value-of select="$number-of-folders-restitution"/>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
									<fo:table-row  border-after-style="solid">
										<fo:table-cell number-columns-spanned="2" padding="5pt">
											<fo:block>
												<xsl:text>Dates extrêmes des documents : </xsl:text>
											</fo:block>
											<fo:block>
												<xsl:text> de </xsl:text><xsl:value-of select="$date-from" />
												<xsl:text> à </xsl:text><xsl:value-of select="$date-to" />
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
									<fo:table-row border-after-style="solid">
										<fo:table-cell number-columns-spanned="2" padding="5pt">
											<fo:block>
												<xsl:text>Poids total des documents (en octets) : </xsl:text>
											</fo:block>
											<fo:block>
												<xsl:value-of select="format-number($size-of-all-files-restitution div 1024, '###''###', 'european')"/><xsl:text> kilo-octets</xsl:text>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
									<fo:table-row>
										<fo:table-cell number-columns-spanned="2" padding="5pt">
											<fo:block>
												<xsl:text>Motif de la restitution : (à compléter par l'archiviste)</xsl:text>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
									<fo:table-row>
										<fo:table-cell number-columns-spanned="2" ><fo:block><fo:leader/></fo:block></fo:table-cell>
									</fo:table-row>
									<fo:table-row>
										<fo:table-cell number-columns-spanned="2" border-after-style="solid"><fo:block><fo:leader/></fo:block></fo:table-cell>
									</fo:table-row>
									<fo:table-row>
										<fo:table-cell number-columns-spanned="2" padding="5pt">
											<fo:block>
												<xsl:text>Bordereau de </xsl:text><fo:page-number-citation-last ref-id="report"/><xsl:text> pages, y compris la présente</xsl:text>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
									<fo:table-row>
										<fo:table-cell number-columns-spanned="2" padding="5pt">
											<fo:block>
												<xsl:text>Date d'édition : </xsl:text><xsl:value-of select="$date-today" />
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
									<fo:table-row border-before-style="solid" text-align="center">
										<fo:table-cell padding="5pt">
											<fo:block>
												<xsl:text>Le responsable du service producteur [Nom, fonction]</xsl:text>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell padding="5pt">
											<fo:block>
												<xsl:text>L'autorité chargée du contrôle scientifique et technique sur les archives publiques [Nom, fonction]</xsl:text>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
									<fo:table-row>
										<fo:table-cell><fo:block><fo:leader/></fo:block></fo:table-cell>
									</fo:table-row>
									<fo:table-row>
										<fo:table-cell><fo:block><fo:leader/></fo:block></fo:table-cell>
									</fo:table-row>
									<fo:table-row>
										<fo:table-cell><fo:block><fo:leader/></fo:block></fo:table-cell>
									</fo:table-row>
									<fo:table-row>
										<fo:table-cell padding="5pt">
											<fo:block>
												<xsl:text>Date de signature : </xsl:text>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell padding="5pt">
											<fo:block>
												<xsl:text>Date de signature : </xsl:text>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
						</fo:block>
					</fo:block-container>
					<fo:block-container page-break-before="always">
						<fo:block>
							<fo:table>
								<fo:table-column column-width="80%" border="solid" />
								<fo:table-column column-width="10%" border="solid" />
								<fo:table-column column-width="10%" border="solid" />
								<fo:table-body>
									<fo:table-row font-weight="bold">
										<fo:table-cell border="solid" padding="5pt">
											<fo:block>
												<xsl:text>Nom et URI des fichiers à restituer</xsl:text>
											</fo:block>
											<fo:block>
												<xsl:text>Nom et URI des dossiers à restituer et nombre de fichiers contenus</xsl:text>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell border="solid" padding="5pt">
											<fo:block>
												<xsl:text>Date de dernière modification</xsl:text>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell border="solid" padding="5pt">
											<fo:block>
												<xsl:text>Poids (en octets)</xsl:text>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
									<xsl:for-each select="//METS:div[@TYPE = 'file']">
										<xsl:variable name="dmd-id" select="./METS:div[@LABEL = 'EAD']/@DMDID"/>
										<xsl:if test="//METS:dmdSec[@ID = $dmd-id]//EAD:c[@otherlevel = 'Restitution']">
											<xsl:call-template name="table-data-files" />
										</xsl:if>
									</xsl:for-each>
									<xsl:for-each select="//METS:div[@TYPE = 'folder']">
										<xsl:variable name="dmd-id" select="./METS:div[@LABEL = 'EAD']/@DMDID"/>
										<xsl:if test="//METS:dmdSec[@ID = $dmd-id]//EAD:c[@otherlevel = 'Restitution']">
											<xsl:call-template name="table-data-folders" />
										</xsl:if>
									</xsl:for-each>
								</fo:table-body>
							</fo:table>
						</fo:block>
					</fo:block-container>	
				</fo:flow>
			</fo:page-sequence>
		</fo:root>
	</xsl:template>



	<xsl:template name="table-data-files">
		<xsl:variable name="adm-id" select="./@ADMID"/>
		<xsl:variable name="file-id" select=".//METS:fptr/@FILEID"/>
		<fo:table-row display-align="before" border="solid">
			<fo:table-cell padding="5pt">
				<fo:block-container page-break-inside="avoid">
					<fo:block>
						<xsl:call-template name="zero_width_space_1">
							<xsl:with-param name="data" select="./@LABEL"/> <!-- file name -->
						</xsl:call-template>
					</fo:block>
					<fo:block>
						<xsl:call-template name="zero_width_space_1">
							<xsl:with-param name="data" select="substring-after(//METS:file[@ID = $file-id]/METS:FLocat[@LOCTYPE='URL']/@xlink:href, 'file:///')" /> <!-- uri of file -->
						</xsl:call-template>
					</fo:block>
				</fo:block-container>
			</fo:table-cell>
			<fo:table-cell padding="5pt">
				<fo:block>
					<xsl:value-of select="format-dateTime((//METS:digiprovMD[@ID = $adm-id]//PREMIS:eventDateTime)[last()], '[D01].[M01].[Y0001]')" /> <!-- date last modified -->
				</fo:block>
			</fo:table-cell>
			<fo:table-cell padding="5pt">
				<fo:block>
					<xsl:value-of select="format-number((//METS:digiprovMD[@ID = $adm-id]//PREMIS:size)[last()] div 1024, '###''###', 'european')" /> <!-- file size -->
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
	</xsl:template>
	<xsl:template name="table-data-folders">
		<xsl:variable name="name-of-folder" select="./@LABEL"/>
		<xsl:variable name="adm-id" select="./@ADMID"/>
		<xsl:variable name="file-id" select="(.//METS:div[@TYPE='file'])[1]//METS:fptr/@FILEID" />
		<fo:table-row display-align="before" border="solid">
			<fo:table-cell padding="5pt">
				<fo:block-container page-break-inside="avoid">
					<fo:block>
						<xsl:call-template name="zero_width_space_1">
							<xsl:with-param name="data" select="./@LABEL"/> <!-- folder name -->
						</xsl:call-template>
					</fo:block>
					<fo:block>
						<xsl:call-template name="zero_width_space_1">
							<xsl:with-param name="data" select="substring-after(concat(substring-before(//METS:file[@ID = $file-id]/METS:FLocat[@LOCTYPE='URL']/@xlink:href, $name-of-folder), $name-of-folder), 'file:///')" /> <!-- uri of folder -->
						</xsl:call-template>
					</fo:block>
					<fo:block>
						<xsl:text>Nombre de dossiers contenus : </xsl:text><xsl:value-of select="count(.//METS:div[@TYPE='folder'])" /> <!-- number of subfolders -->
					</fo:block>
					<fo:block>
						<xsl:text>Nombre de fichiers contenus : </xsl:text><xsl:value-of select="count(.//METS:div[@TYPE='file'])" /> <!-- number of files inside folder -->
					</fo:block>
				</fo:block-container>
			</fo:table-cell>
			<fo:table-cell padding="5pt">
				<fo:block>
					<xsl:value-of select="format-dateTime((//METS:digiprovMD[@ID = $adm-id]//PREMIS:eventDateTime)[last()], '[D01].[M01].[Y0001]')" /> <!-- date last modified -->
				</fo:block>
			</fo:table-cell>
			<fo:table-cell padding="5pt">
				<fo:block>
					<xsl:variable name="adm-id-local" select=".//METS:div[@TYPE='file']/@ADMID" />
					<xsl:value-of select="format-number(xs:decimal(sum(//METS:digiprovMD[@ID = $adm-id-local]//PREMIS:size)) div 1024, '###''###', 'european')"/>
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
	</xsl:template>

	<!-- templates to set line breaks in long strings in case of table cell overflow -->
	<xsl:template name="zero_width_space_1">
		<xsl:param name="data"/>
		<xsl:param name="counter" select="0"/>
		<xsl:choose>
			<xsl:when test="$counter &lt;= string-length($data)">
				<xsl:value-of select='concat(substring($data,$counter,1),"&#8203;")'/>
				<xsl:call-template name="zero_width_space_2">
					<xsl:with-param name="data" select="$data"/>
					<xsl:with-param name="counter" select="$counter+1"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise />
		</xsl:choose>
	</xsl:template>

	<xsl:template name="zero_width_space_2">
		<xsl:param name="data"/>
		<xsl:param name="counter"/>
		<xsl:value-of select='concat(substring($data,$counter,1),"&#8203;")'/>
		<xsl:call-template name="zero_width_space_1">
			<xsl:with-param name="data" select="$data"/>
			<xsl:with-param name="counter" select="$counter+1"/>
		</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>

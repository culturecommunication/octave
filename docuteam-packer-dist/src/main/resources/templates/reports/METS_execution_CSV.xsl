<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:METS="http://www.loc.gov/METS/"
    xmlns:PREMIS="info:lc/xmlns/premis-v2"
    xmlns:EAD="urn:isbn:1-931666-22-9"
    xmlns:xlink="http://www.w3.org/1999/xlink" 
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xsi:schemaLocation="urn:isbn:1-931666-22-9 http://www.loc.gov/ead/ead.xsd   http://www.w3.org/1999/xlink http://www.loc.gov/standards/mets/xlink.xsd">
    
    <xsl:output method="text" encoding="UTF-8"/>
    <xsl:strip-space elements="*" />
    
    <xsl:variable name="delimiter" select="','" />
    <xsl:variable name="quote" select="'&quot;'" />
    <xsl:variable name="column-headers">
        <xsl:value-of select="$quote" />
        <xsl:text>URI</xsl:text>
        <xsl:value-of select="$quote" />
        <xsl:value-of select="$delimiter" />
        <xsl:value-of select="$quote" />
        <xsl:text>Type d'erreur</xsl:text>
        <xsl:value-of select="$quote" />
        <xsl:value-of select="$delimiter" />
        <xsl:value-of select="$quote" />
        <xsl:text>Horodatage</xsl:text>
        <xsl:value-of select="$quote" />
        <xsl:text>&#xD;&#xA;</xsl:text>
    </xsl:variable>
    <xsl:variable name="error-message-puid-unknown">
        <xsl:text>PUID non identifié</xsl:text>
    </xsl:variable>
    <xsl:variable name="error-message-mime-type-unknown">
        <xsl:text>Type MIME non identifié</xsl:text>
    </xsl:variable>
    <xsl:variable name="error-message-both-types-unknown">
        <xsl:text>PUID non identifié, type MIME non identifié</xsl:text>
    </xsl:variable>
    
    <xsl:template match="/">
        <xsl:value-of select="$column-headers"/>
        <xsl:for-each select="//METS:div[@TYPE = 'file']">
            <xsl:if test="not(ancestor::METS:div[@LABEL = 'Traçabilité des opérations réalisées pendant le traitement'])">
                <xsl:call-template name="csv-data" />
            </xsl:if>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template name="csv-data">
        <xsl:variable name="adm-id" select="@ADMID"/>
        <xsl:variable name="file-id" select=".//METS:fptr/@FILEID"/> 
        <xsl:variable name="dmd-id" select=".//METS:div/@DMDID"/>
        
        <!-- no mime type, no puid -->
        <xsl:if test="((//METS:file[@ID = $file-id]/@MIMETYPE = '') and (//METS:digiprovMD[@ID = $adm-id]//PREMIS:formatRegistryKey = ''))">
            <xsl:value-of select="$quote" />
            <xsl:value-of select="substring-after(//METS:file[@ID = $file-id]/METS:FLocat[@LOCTYPE='URL']/@xlink:href, 'file:///')" /> <!-- uri of file -->
            <xsl:value-of select="$quote" />
            <xsl:value-of select="$delimiter" />
            <xsl:value-of select="$quote" />
            <xsl:value-of select="$error-message-both-types-unknown"/>
            <xsl:value-of select="$quote" />
            <xsl:value-of select="$delimiter" />
            <xsl:value-of select="$quote" />
            <xsl:value-of select="//METS:digiprovMD[@ID=$adm-id]//PREMIS:event[1]/PREMIS:eventDateTime"/> <!-- date from first premis event (creation) -->
            <xsl:value-of select="$quote" />
            <xsl:text>&#xD;&#xA;</xsl:text>
        </xsl:if>
        
        <!-- no mime type but puid -->
        <xsl:if test="((//METS:file[@ID = $file-id]/@MIMETYPE = '') and (//METS:digiprovMD[@ID = $adm-id]//PREMIS:formatRegistryKey != ''))">
            <xsl:value-of select="$quote" />
            <xsl:value-of select="substring-after(//METS:file[@ID = $file-id]/METS:FLocat[@LOCTYPE='URL']/@xlink:href, 'file:///')" /> <!-- uri of file -->
            <xsl:value-of select="$quote" />
            <xsl:value-of select="$delimiter" />
            <xsl:value-of select="$quote" />
            <xsl:value-of select="$error-message-mime-type-unknown"/>
            <xsl:value-of select="$quote" />
            <xsl:value-of select="$delimiter" />
            <xsl:value-of select="$quote" />
            <xsl:value-of select="//METS:digiprovMD[@ID=$adm-id]//PREMIS:event[1]/PREMIS:eventDateTime"/> <!-- date from first premis event (creation) -->
            <xsl:value-of select="$quote" />
            <xsl:text>&#xD;&#xA;</xsl:text>
        </xsl:if>
        
        <!-- no puid but mime type -->
        <xsl:if test="((//METS:file[@ID = $file-id]/@MIMETYPE != '') and (//METS:digiprovMD[@ID = $adm-id]//PREMIS:formatRegistryKey = ''))">
            <xsl:value-of select="$quote" />
            <xsl:value-of select="substring-after(//METS:file[@ID = $file-id]/METS:FLocat[@LOCTYPE='URL']/@xlink:href, 'file:///')" /> <!-- uri of file -->
            <xsl:value-of select="$quote" />
            <xsl:value-of select="$delimiter" />
            <xsl:value-of select="$quote" />
            <xsl:value-of select="$error-message-puid-unknown"/>
            <xsl:value-of select="$quote" />
            <xsl:value-of select="$delimiter" />
            <xsl:value-of select="$quote" />
            <xsl:value-of select="//METS:digiprovMD[@ID=$adm-id]//PREMIS:event[1]/PREMIS:eventDateTime"/> <!-- date from first premis event (creation) -->
            <xsl:value-of select="$quote" />
            <xsl:text>&#xD;&#xA;</xsl:text>
        </xsl:if>
    </xsl:template>
</xsl:stylesheet>
/*
 * Copyright (C) since 2011 by docuteam AG
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package ch.docuteam.packer.util;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import ch.docuteam.tools.out.Logger;

import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;

/**
 * @author andreas
 */
public abstract class XML2PDF {

    public static File createPDF(final String xml, final String xslt) throws IOException {
        return createPDF(new File(xml), new File(xslt));
    }

    public static File createPDF(final File xml, final File xslt) throws IOException {
        final File pdf = new File(xml.getParent() + "/" + xml.getName().replace("xml", "pdf"));
        return createPDF(xml, xslt, pdf);
    }

    public static File createPDF(final String xml, final String xslt, final String pdf) throws IOException {
        return createPDF(new File(xml), new File(xslt), new File(pdf));
    }

    public static File createPDF(final File xml, final File xslt, final File pdf) throws IOException {
        Logger.info("Initializing transformation...");
        pdf.getParentFile().mkdirs();
        final OutputStream out = new java.io.BufferedOutputStream(new java.io.FileOutputStream(pdf));

        Logger.info("Input: XML (" + xml + ")");
        Logger.info("Stylesheet: " + xslt);
        Logger.info("Output: PDF (" + pdf + ")");
        Logger.info("Transforming...");

        final FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());

        try {
            // Construct fop with desired output format
            final Fop fop = fopFactory.newFop(org.apache.xmlgraphics.util.MimeConstants.MIME_PDF, out);

            // Setup XSLT
            final TransformerFactory factory = new net.sf.saxon.TransformerFactoryImpl();
            final Transformer transformer = factory.newTransformer(new StreamSource(xslt));

            // Set the value of a <param> in the stylesheet
            transformer.setParameter("versionParam", "2.0");

            // Setup input for XSLT transformation
            final Source src = new StreamSource(xml);

            // Resulting SAX events (the generated FO) must be piped through to
            // FOP
            final Result res = new SAXResult(fop.getDefaultHandler());

            // Start XSLT transformation and FOP processing
            transformer.transform(src, res);
        } catch (final FOPException e) {
            Logger.error(e.getMessage(), e);
            return null;
        } catch (final TransformerConfigurationException e) {
            Logger.error(e.getMessage(), e);
            return null;
        } catch (final TransformerException e) {
            Logger.error(e.getMessage(), e);
            return null;
        } finally {
            out.close();
        }

        return pdf;
    }

}

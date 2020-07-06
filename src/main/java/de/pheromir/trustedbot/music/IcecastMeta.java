/*******************************************************************************
 * Copyright (C) 2019 Pheromir
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package de.pheromir.trustedbot.music;

import de.pheromir.trustedbot.Main;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IcecastMeta {

    private URL url;
    private final DocumentBuilderFactory factory;
    private DocumentBuilder builder;
    private Document doc;
    private XPathFactory xpathFactory;
    private XPath xpath;

    public IcecastMeta(String xspfUrl) {
        factory = DocumentBuilderFactory.newInstance();
        doc = null;
        try {
            url = new URL(xspfUrl);
            InputStream is = url.openStream();
            builder = factory.newDocumentBuilder();
            doc = builder.parse(is);
            xpathFactory = XPathFactory.newInstance();

            xpath = xpathFactory.newXPath();
        } catch (Exception e1) {
            Main.LOG.error("", e1);
            xpath = null;
        }
    }

    public String getTitle() {
        if (xpath == null) {
            return "Unknown (Error)";
        }
        try {
            XPathExpression expr = xpath.compile("//track/title/text()");
            String res = expr.evaluate(doc);
            if (res != null) {
                return res;
            } else
                return "Unbekannt";
        } catch (XPathExpressionException e) {
            Main.LOG.error("", e);
        }
        return null;
    }

    public int getCurrentListeners() {
        if (xpath == null) {
            return -1;
        }
        try {
            XPathExpression expr = xpath.compile("//annotation/text()");
            String res = expr.evaluate(doc);
            if (res != null) {
                Pattern p = Pattern.compile("Current Listeners: (\\d+)");
                Matcher m = p.matcher(res);
                if (m.find()) {
                    return Integer.parseInt(m.group(1));
                }
            }
        } catch (XPathExpressionException e) {
            Main.LOG.error("", e);
        }
        return -1;
    }

}

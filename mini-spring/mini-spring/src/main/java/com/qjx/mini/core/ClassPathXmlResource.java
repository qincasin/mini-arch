package com.qjx.mini.core;

import java.net.URL;
import java.util.Iterator;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * <Description>
 *
 * @author qinjiaxing on 2023/5/9
 * @author <others>
 */
public class ClassPathXmlResource implements Resource {

    private Document document;
    private Element rootElement;
    private Iterator iterator;

    public ClassPathXmlResource(String fileName) {
        SAXReader saxReader = new SAXReader();
        URL xmlResource = this.getClass().getClassLoader().getResource(fileName);
        try {
            this.document = saxReader.read(xmlResource);
            this.rootElement = this.document.getRootElement();
            this.iterator = this.rootElement.elementIterator();
        } catch (Exception e) {
        }
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public Object next() {
        return iterator.next();
    }
}

package com.mephone.lovelynote.inkml;

import android.text.TextUtils;
import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author huanghua
 */
public class InkMLHandler extends DefaultHandler {

    private List<Ink> mInks;
    private Ink mCurrentInk;
    private String mTagName;
    private StringBuffer mTraceData;

    public List<Ink> getInks() {
        return mInks;
    }


    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }

    //接收文档开始的通知。当遇到文档的开头的时候，调用这个方法，可以在其中做一些预处理的工作。
    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        mInks = new ArrayList<>();
    }

    //接收元素开始的通知。当读到一个开始标签的时候，会触发这个方法。其中namespaceURI表示元素的命名空间；
    //localName表示元素的本地名称（不带前缀）；qName表示元素的限定名（带前缀）；atts 表示元素的属性集合
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        if (localName.equals("ink")) {
            mCurrentInk = new Ink();
        } else if (localName.equals("trace")) {
            mTraceData = new StringBuffer();
        }
        mTagName = localName;
    }


    //接收文档的结尾的通知。在遇到结束标签的时候，调用这个方法。其中，uri表示元素的命名空间；
    //localName表示元素的本地名称（不带前缀）；name表示元素的限定名（带前缀）
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        if (localName.equals("ink")) {
            mInks.add(mCurrentInk);
            mCurrentInk = null;
        } else if (localName.equals("trace")) {
            if (mTraceData != null) {
                Trace trace = new Trace();
                String data = mTraceData.toString().replace("\n","");
                String[] lines = data.split(",");
                for (String line : lines) {
                    String[] points = line.trim().split(" ");
                    Point point = new Point(points);
                    trace.addPoints(point);
                }
                mCurrentInk.addTraces(trace);
            }
            mTraceData = null;
        }
        mTagName = null;
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        super.endPrefixMapping(prefix);
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        super.startPrefixMapping(prefix, uri);
    }

    //接收字符数据的通知。该方法用来处理在XML文件中读到的内容，第一个参数用于存放文件的内容，
    //后面两个参数是读到的字符串在这个数组中的起始位置和长度，使用new String(ch,start,length)就可以获取内容。
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        if (mTagName != null) {
            String data = new String(ch, start, length);
            if (mTagName.equals("trace")) {
                mTraceData.append(data);
            }
        }
    }

}

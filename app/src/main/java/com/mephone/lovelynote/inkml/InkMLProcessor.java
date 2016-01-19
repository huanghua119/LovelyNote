package com.mephone.lovelynote.inkml;

import android.util.Log;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * @author huanghua
 */
public class InkMLProcessor {

    public interface InkMLProcessorListener {
        void onSuccess(List<Ink> inks);

        void onFail(Exception e);
    }

    private SAXParserFactory mSaxParserFactory;
    private InkMLHandler mInkHandler = new InkMLHandler();
    private InkMLProcessorListener mListener;

    public InkMLProcessor() {
        mSaxParserFactory = SAXParserFactory.newInstance();
    }

    public void setListener(InkMLProcessorListener listener) {
        this.mListener = listener;
    }

    public void parseInkMLFile(InputSource inputSource) {
        try {
            SAXParser saxParser = mSaxParserFactory.newSAXParser();
            saxParser.parse(inputSource, mInkHandler);
            List<Ink> inks = mInkHandler.getInks();
            if (mListener != null) {
                mListener.onSuccess(inks);
            }
        } catch (ParserConfigurationException e) {
            if (mListener != null) {
                mListener.onFail(e);
            }
        } catch (SAXException e) {
            if (mListener != null) {
                mListener.onFail(e);
            }
        } catch (IOException e) {
            if (mListener != null) {
                mListener.onFail(e);
            }
        }
    }

    public void parseInkMLFile(String path) {
        try {
            SAXParser saxParser = mSaxParserFactory.newSAXParser();
            saxParser.parse(new File(path), mInkHandler);
            List<Ink> inks = mInkHandler.getInks();
            if (mListener != null) {
                mListener.onSuccess(inks);
            }
        } catch (ParserConfigurationException e) {
            if (mListener != null) {
                mListener.onFail(e);
            }
        } catch (SAXException e) {
            if (mListener != null) {
                mListener.onFail(e);
            }
        } catch (IOException e) {
            if (mListener != null) {
                mListener.onFail(e);
            }
        }
    }

    public void parseInkMLFile(InputStream is) {
        parseInkMLFile(new InputSource(is));
    }
}

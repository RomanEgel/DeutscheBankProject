package com.twoez.common;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.Queue;

public class HTMLReaderHelper {

    private final URL currentURL;

    public HTMLReaderHelper(URL url){
        currentURL = url;
    }

    public CharIterator charIterator(){
        CharIterator iterator;
        try {
            iterator = new CharIterator();
        } catch(IOException ex){
            return null;
        }
        return iterator;
    }

    public StringIterator stringIterator(){
        return new StringIterator();
    }

    public class CharIterator implements java.util.Iterator<Character>{

        private InputStream urlStream;

        private boolean isValid;

        private Queue<Character> buffer;

        private CharIterator() throws IOException {
            while (urlStream == null) {
                urlStream = currentURL.openStream();
            }
            isValid = true;
            buffer = new ArrayDeque<>();
        }

        @Override
        public boolean hasNext() {
            char c;
            try {
                c = (char)urlStream.read();
                buffer.add(c);
            } catch (IOException ex) {
                markInvalid();
                return false;
            }
            return c != (char) -1;
        }

        @Override
        public Character next() {
            if(!isValid){
                return null;
            }
            char c;
            try {
                if(buffer.size() > 0){
                    return buffer.remove();
                }
                c = (char)urlStream.read();
            } catch (IOException ex) {
                markInvalid();
                return null;
            }
            return (c != (char)-1) ? c : null;
        }

        private void markInvalid(){
            isValid = false;
        }
    }

    public class StringIterator implements java.util.Iterator<String>{

        private CharIterator charPointer;

        private Queue<String> buffer;

        private boolean isValid;

        private StringIterator(){
            charPointer = charIterator();
            isValid = true;
            buffer = new ArrayDeque<>();
        }

        @Override
        public boolean hasNext() {
            String value = next();
            try {
                buffer.add(value);
            } catch (NullPointerException ex){
                markInvalid();
                return false;
            }
            return isValid;
        }

        @Override
        public String next() {
            if(buffer.size() > 0){
                return buffer.remove();
            }
            if(!isValid){
                return null;
            }
            StringBuilder sb = new StringBuilder();

            Character currentChar = charPointer.next();
            if(currentChar == null){
                return null;
            }
            while (currentChar.equals('\n') || currentChar.equals('\r')){
                currentChar = charPointer.next();
                if(currentChar == null){
                    return null;
                }
            }
            while (currentChar != Character.valueOf('\n') && currentChar != Character.valueOf('\r') && currentChar != null){
                sb.append(currentChar);
                currentChar = charPointer.next();
            }
            return sb.toString();
        }
        private void markInvalid(){
            isValid = false;
        }
    }
}

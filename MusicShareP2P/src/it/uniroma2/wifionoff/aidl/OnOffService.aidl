package it.uniroma2.wifionoff.aidl;

interface OnOffService {
      void doServiceTask();
      
      boolean removeMe(String s);
      
      boolean addMe(String name, String Bro);
}
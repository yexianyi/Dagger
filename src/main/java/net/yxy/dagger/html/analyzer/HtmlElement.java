package net.yxy.dagger.html.analyzer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Tag;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;

public class HtmlElement {
	private Element element ;
	
	public HtmlElement(Element elem) {
		element = elem ;
	}
	
	public String text() {
		final StringBuilder accum = new StringBuilder();
        new NodeTraversor(new NodeVisitor() {
            public void head(Node node, int depth) {
                if (node instanceof TextNode) {
                    TextNode textNode = (TextNode) node;
                    //element.appendNormalisedText(accum, textNode);
					try {
						Method method = element.getClass().getDeclaredMethod("appendNormalisedText", new Class[]{StringBuilder.class, TextNode.class});
						method.setAccessible(true);
						method.invoke(null, new Object[]{accum, textNode}); 
						method.setAccessible(false);
					} catch (Exception e) {
						e.printStackTrace();
					} 

                } else if (node instanceof Element) {
                    Element element = (Element) node;
                	try {
                		Field field=element.getClass().getDeclaredField("tag");
                		field.setAccessible(true);
                		Tag tag = (Tag) field.get(element) ;
                		field.setAccessible(false);
						Method method = TextNode.class.getDeclaredMethod("lastCharIsWhitespace", new Class[]{StringBuilder.class});
						method.setAccessible(true);
						 if (accum.length() > 0 && (element.isBlock() || tag.getName().equals("br")) && !(boolean)method.invoke(null, new Object[]{accum})){
							 accum.append(" ");
						 }
						 method.setAccessible(false);
					} catch (Exception e) {
						e.printStackTrace();
					} 

                   
                }
            }
            
            
            public void tail(Node node, int depth) {
            	if (node instanceof Element){
            		//find node tag name
            		Element element = (Element) node;
            		String nodeTagName = null ;
            		
            		try{
                		Field field=element.getClass().getDeclaredField("tag");
                		field.setAccessible(true);
                		Tag tag = (Tag) field.get(element) ;
                		field.setAccessible(false);
                		nodeTagName = tag.getName();
            		} catch (Exception e){
            			e.printStackTrace();
            		}
            		
            		//find the last valid char in a sentence
            		int endIdx = accum.length()-1 ;
            		while(endIdx>0 && (accum.charAt(endIdx)==',' || accum.charAt(endIdx)=='.' || accum.charAt(endIdx)==' ')){
            				endIdx-- ;
            		}
    				endIdx++ ;
            		
            		if(endIdx >= 0 && endIdx<accum.length()){
            			char lastChar = accum.charAt(endIdx) ;
                		
                		if(nodeTagName.equalsIgnoreCase("tr")){
            				if(lastChar == ',' || lastChar == ' '){
            					accum.replace(endIdx, accum.length(), ".") ;
            				}else if(isTerminateChar(lastChar)){
            					//do nothing
            				}else{
            					accum.append(".") ;
            				}
            			}else if((nodeTagName.equalsIgnoreCase("td") || nodeTagName.equalsIgnoreCase("li"))){
            				if(!isTerminateChar(accum.charAt(endIdx))){
            					accum.append(",") ;
            				}
        				}
            		}
            		
            		
            	}
            }
        }).traverse(element);
        return accum.toString().trim();
	}
	
	private boolean isTerminateChar(char ch){
		if(ch=='!' || ch=='.' || ch=='?' || Character.isAlphabetic(ch)){
			return true ;
		}
		
		return false ;
	}

}

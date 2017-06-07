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
                		Field field = element.getClass().getDeclaredField("tag");
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
						//ignore
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
                		
                		int endIdx = findLastValidCharIdx(accum) ;
                		switch(nodeTagName){
	                		case "li":
	                		case "td":
	                			if(!isPunctuation(accum.charAt(endIdx)) || accum.charAt(endIdx)==')'){
	                				accum.replace(endIdx+1, accum.length(), ",") ;
	                			}
	                			
	                			break ;
	                		case "h1":
	                		case "h2":
	                		case "h3":
	                		case "h4":
	                		case "h5":
	                		case "h6":
	                		case "tr":
	                		case "ul":
	                			if(accum.charAt(endIdx)==','){
	                				accum.replace(endIdx, accum.length(), ".") ;
	                			}else if(!isPunctuation(accum.charAt(endIdx))){
	                				accum.replace(endIdx+1, accum.length(), ".") ;
	                			}
	                			break ;
                		}
                		
            		} catch (Exception e){
            			//ignore
            		}
            		
            	}//end Element
            }
        }).traverse(element);
        return accum.toString().trim();
	}
	
	private boolean isPunctuation(char ch){
		if((ch>32 && ch<48) || (ch>57 && ch<65) || (ch>90 && ch<97) || (ch>122 && ch<127)){
			return true ;
		}
		
		return false ;
	}
	
	private int findLastValidCharIdx(StringBuilder sb){
		int endIdx = sb.length()-1 ;
		while(endIdx>=0 && Character.isWhitespace(sb.charAt(endIdx))){
    			endIdx-- ;
    	}
		
		return endIdx ;
	}
	
	private boolean isTerminateChar(char ch){
		if(ch==',' || ch=='.' || ch=='?' || ch==':'){
			return true ;
		}
		
		return false ;
	}

}

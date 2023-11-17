public class Symbol{
	public static final int UNDEFINED_POSITION = -1;
	public static final Object NO_VALUE = null;
	
	private final LexicalUnit type;
	private final Object value;
	private final int line,column;
	private final String name;

	public Symbol(LexicalUnit unit,int line,int column,Object value, String name){
		this.type	= unit;
		this.line	= line+1;
		this.column	= column;
		this.value	= value;
		this.name = name;
	}

	public Symbol(LexicalUnit unit,int line,int column,Object value){
		this.type	= unit;
		this.line	= line+1;
		this.column	= column;
		this.value	= value;
		this.name = null;
	}
	
	public Symbol(LexicalUnit unit,int line,int column){
		this(unit,line,column,NO_VALUE, null);
	}
	
	public Symbol(LexicalUnit unit,int line){
		this(unit,line,UNDEFINED_POSITION,NO_VALUE, null);
	}
	
	public Symbol(LexicalUnit unit){
		this(unit,UNDEFINED_POSITION,UNDEFINED_POSITION,NO_VALUE, null);
	}
	
	public Symbol(LexicalUnit unit,Object value){
		this(unit,UNDEFINED_POSITION,UNDEFINED_POSITION,value, null);
	}

	public Symbol(String name){
		this(null, UNDEFINED_POSITION, UNDEFINED_POSITION, NO_VALUE, name);
	}

	public boolean isTerminal(){
		return this.type != null;
	}
	
	public boolean isNonTerminal(){
		return this.type == null;
	}

	public boolean hasValue(){
		return this.value != null;
	}
	
	public LexicalUnit getType(){
		return this.type;
	}
	
	public Object getValue(){
		return this.value;
	}
	
	public int getLine(){
		return this.line;
	}
	
	public int getColumn(){
		return this.column;
	}

	public String getName(){
		return this.name;
	}

	public String toTexString(){
		if(this.isNonTerminal()){
			return this.name;
		}
		if(hasValue()){
			if(this.type.equals(LexicalUnit.VARNAME)){
				return "[VarName]";
			}
			else if(this.type.equals(LexicalUnit.NUMBER)){
				return "[Number]";
			}
			else if(this.type.equals(LexicalUnit.SMALLER)){
				return "$<$";
			}
			else{
				return this.value.toString();
			}
			
		}
		return (this.type).toString();
	}
	
	@Override
	public int hashCode(){
		final String value	= this.value != null? this.value.toString() : "null";
		final String type		= this.type  != null? this.type.toString()  : "null";
		return new String(value+"_"+type).hashCode();
	}
	
	@Override
	public String toString(){
		if(this.isTerminal()){
			final String value	= this.value != null? this.value.toString() : "null";
			final String type		= this.type  != null? this.type.toString()  : "null";
			return "token: "+value+"\tlexical unit: "+type;
		}
		return "Non-terminal symbol";
	}
}

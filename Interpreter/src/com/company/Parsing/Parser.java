package com.company.Parsing;

import com.company.Enums.Term;
import com.company.Tokens.Token;
import jdk.nashorn.internal.runtime.ParserException;

import java.util.List;

public class Parser
{
    /*
	* expr -> method | LINE_END
	* expr_body -> body | LINE_END
	* value -> ( ( const_value | NAME | call) (MATH_OP value)? ) | ( OPENING_BRACKET value OPENING_BRACKET )
	* body -> OPENING_BODY expr_body CLOSING_BODY
	* method -> VAR_TYPE NAME OPENING_BRACKET (define_var_simple (COMMA define_var_simple)* )? CLOSING_BRACKET body
	* define_var_simple -> VAR_TYPE NAME
	* define_var -> define_var_simple (ASSIGN_OP value)? LINE_END
	* var_assign -> VAR_NAME ASSIGN_OP value LINE_END
	* var_unary -> (VAR_NAME UNARY_OP | UNARY_OP VAR_NAME) LINE_END
	* if - > IF OPENING_BRACKET condition CLOSING_BRACKET body
	* condition -> value CONDITION_OP value
	* while -> WHILE OPENING_BRACKET condition CLOSING_BRACKET body
	* for -> FOR OPENING_BRACKET init_area LINE_END condition LINE_END inc_area CLOSING_BRACKET body
	* const_value -> NATURAL_DIGIT | DIGIT | BOOLEAN
	* return_op -> RETURN VAR_NAME? LINE_END
	* call -> NAME OPENING_BRACKET (value (COMMA value)* )? CLOSING_BRACKET LINE_END
	* call_end -> call LINE_END
    */

    private Token currentToken;
    private int currentIndex;
    private List<Token> tokens;

    public Parser(List<Token> tokens)
    {
        this.tokens = tokens;
        Run();
    }

    private void Run() throws ParserException {
        try{
            for(int i = 0; i < tokens.size(); i++) {

            }
        }
        catch (ParserException ex)
        {

        }
    }

    //expr -> method | LINE_END
    private boolean Expr(ParserFunction function) throws  ParserException
    {
        return true;
    }

    @FunctionalInterface
    interface ParserFunction
    {
        void invoke(Parser parser);
    }

    private boolean TypeIsEqual(Term type)throws ParserException {
        return GetCurrentToken().GetLexeme().GetTerminal() == type;
    }

    private Token GetCurrentToken() throws ParserException {
        if(tokens.isEmpty() || currentIndex >= tokens.size())
            throw new ParserException("Попытка считать несуществующий токен!");
        return tokens.get(currentIndex);
    }
}

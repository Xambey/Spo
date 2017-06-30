package com.company.Parsing;

import com.company.Enums.Term;
import com.company.Exceptions.BuildExeption;
import com.company.Exceptions.SyntaxBuilderException;
import com.company.ProgramThree.*;
import com.company.Tokens.Token;
import jdk.nashorn.internal.ir.Terminal;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;
import sun.nio.cs.ext.ISCII91;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.company.Enums.Term.*;

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
    private int currentTokenIndex;
    private List<Token> tokens;
    private RootNode rootNode;
    private MethodNode currentMethodNode;

    RootBodyNode run(List<Token> tokens) throws BuildExeption
    {
        errors = new ArrayList<>();
        nodes = new Stack<>();
        this.tokens = tokens;

        RootBodyNode root = new RootBodyNode();
        nodes.push(root);

        expr();

        return root;
    }

    // expr -> method | LINE_END
    private void expr() throws BuildExeption
    {
        while(tokens.size() > index)
        {
            errors.clear();

            if(check(Terminals.BODY_CLOSE))
                return;

            if(	tryStep(Parcer::method) ||
                    tryStep(Parcer::line_end))
                continue;

            if(!errors.isEmpty())
            {
                errors.sort((a, b) -> -a.getKey().compareTo(b.getKey()));

                index = errors.get(0).getKey();
                String msg = errors.get(0).getValue();
                System.out.printf("%s, символ %d (%s)\n", msg, current().getStart(), current().getValue());
            }
            else
            {
                System.out.printf("Неверная синтаксическая конструкция, символ %d (%s)\n", current().getStart(), current().getValue());
            }

            step();
        }
    }

    // expr_body -> var_def | var_unar | while | if | var_assign | call_e | body | return_op | LINE_END
    private void expr_body() throws BuildExeption
    {
        while(tokens.size() > index)
        {
            errors.clear();

            if(check(Terminals.BODY_CLOSE))
                return;

            if(	tryStep(Parcer::var_def) ||
                    tryStep(Parcer::var_assign)	||
                    tryStep(Parcer::var_unar) ||
                    tryStep(Parcer::if_operator) ||
                    tryStep(Parcer::while_operator)	||
                    tryStep(Parcer::call_e) ||
                    tryStep(Parcer::body) ||
                    tryStep(Parcer::return_op) ||
                    tryStep(Parcer::line_end))
                continue;

            step();
        }
    }

    // body -> BODY_OPEN expr BODY_CLOSE
    private void body() throws BuildExeption
    {
        addAndPushNode(new BodyNode());
        checkAndStep(Terminals.BODY_OPEN);
        expr_body();
        checkAndStep(Terminals.BODY_CLOSE);
        nodes.pop();
    }

    // method -> VAR_TYPE NAME BRACED_OPEN (var_def_simple (COMMA var_def_simple)* )? BRACED_CLOSE body
    private void method() throws BuildExeption
    {
        String type = checkAndStep(Terminals.VAR_TYPE);
        String name = checkAndStep(Terminals.NAME);
        checkAndStep(Terminals.BRACED_OPEN);

        MethodNode node = new MethodNode(type, name);
        addAndPushNode(node);

        if(!check(Terminals.BRACED_CLOSE))
        {
            nodes.push(node.VariablesBody);
            do
            {
                var_def_simple();
            }
            while(stepIF(Terminals.COMMA));

            nodes.pop();
        }

        checkAndStep(Terminals.BRACED_CLOSE);

        checkAndStep(Terminals.BODY_OPEN);
        expr_body();
        checkAndStep(Terminals.BODY_CLOSE);
        nodes.pop();
    }

    // while -> WHILE_OP BRACED_OPEN condition BRACED_CLOSE body
    private void while_operator() throws BuildExeption
    {
        WhileNode loop = new WhileNode();
        addAndPushNode(loop);

        checkAndStep(Terminals.WHILE_OP);
        checkAndStep(Terminals.BRACED_OPEN);
        loop.condition = condition();
        checkAndStep(Terminals.BRACED_CLOSE);

        checkAndStep(Terminals.BODY_OPEN);
        expr_body();
        checkAndStep(Terminals.BODY_CLOSE);
        nodes.pop();
    }

    // IF_OP BRACED_OPEN condition BRACED_CLOSE body
    private void if_operator() throws BuildExeption
    {
        BranchNode branch = new BranchNode();
        addAndPushNode(branch);

        checkAndStep(Terminals.IF_OF);
        checkAndStep(Terminals.BRACED_OPEN);
        branch.condition = condition();
        checkAndStep(Terminals.BRACED_CLOSE);

        checkAndStep(Terminals.BODY_OPEN);
        expr_body();
        checkAndStep(Terminals.BODY_CLOSE);

        if(stepIF(Terminals.ELSE))
        {
            branch.elseBody = new BodyNode();
            nodes.push(branch.elseBody);

            checkAndStep(Terminals.BODY_OPEN);
            expr_body();
            checkAndStep(Terminals.BODY_CLOSE);

            nodes.pop();
        }

        nodes.pop();
    }

    // condition -> value CONDITION_OP value
    private ConditionNode condition() throws BuildExeption
    {
        ConditionNode node = new ConditionNode();

        nodes.push(node.left);
        value();
        nodes.pop();

        node.operator = checkAndStep(Terminals.CONDITION_OP);

        nodes.push(node.right);
        value();
        nodes.pop();

        return node;
    }

    // call -> call LINE_END
    private void call_e()throws BuildExeption
    {
        call();
        checkAndStep(Terminals.LINE_END);
    }

    // call -> NAME BRACED_OPEN (value (COMMA value)* )? BRACED_CLOSE
    private void call()throws BuildExeption
    {
        String name = checkAndStep(Terminals.NAME);
        checkAndStep(Terminals.BRACED_OPEN);

        addAndPushNode(new CallNode(name));

        if(!check(Terminals.BRACED_CLOSE))
        {
            do
            {
                addAndPushNode(new ValueBodyNode());
                value();
                nodes.pop();
            }
            while(stepIF(Terminals.COMMA));
        }

        checkAndStep(Terminals.BRACED_CLOSE);
        nodes.pop();
    }

    // var_def_simple -> VAR_TYPE NAME
    private void var_def_simple() throws BuildExeption
    {
        String type = checkAndStep(Terminals.VAR_TYPE);
        String name = checkAndStep(Terminals.NAME);

        addNode(new VarDefineNode(type, name));
    }

    // var_def -> var_def_simple ASSIGN_OP value LINE_END
    private void var_def() throws BuildExeption
    {
        var_def_simple();
        String name = tokens.get(index - 1).getValue();

        if(stepIF(Terminals.ASSIGN_OP))
        {
            addAndPushNode(new AssignNode(name));
            value();
            nodes.pop();
        }

        checkAndStep(Terminals.LINE_END);
    }

    // var_assign -> VAR_NAME ASSIGN_OP value LINE_END
    private void var_assign() throws BuildExeption
    {
        String name = checkAndStep(Terminals.NAME);
        String operator = checkAndStep(Terminals.ASSIGN_OP);

        addAndPushNode(new AssignNode(name));

        if(!Objects.equals(operator, "="))
        {
            addNode(new VarNameNode(name));
            addNode(new MathOperationNode(operator.substring(0, 1)));
            addAndPushNode(new ValueBodyNode());
            value();
            nodes.pop();
        }
        else
            value();

        nodes.pop();

        checkAndStep(Terminals.LINE_END);
    }

    // var_unar -> VAR_NAME UNAR_OP LINE_END
    private void var_unar() throws BuildExeption
    {
        String name = checkAndStep(Terminals.NAME);
        String operator = checkAndStep(Terminals.UNAR_OP);

        addNode(new VarUnarNode(name, operator));

        checkAndStep(Terminals.LINE_END);
    }

    // return -> RETURN VAR_NAME? LINE_END
    private void return_op() throws BuildExeption
    {
        addAndPushNode(new ReturnNode());

        if(!check(Terminals.LINE_END))
            value();

        nodes.pop();

        checkAndStep(Terminals.LINE_END);
    }

    // value -> ( BRACED_OPEN value BRACED_OPEN ) | ( (const_value|NAME|call) (MATH_OP value)? )
    private void value() throws BuildExeption
    {
        if(stepIF(Terminals.BRACED_OPEN))
        {
            addAndPushNode(new ValueBodyNode());
            value();
            checkAndStep(Terminals.BRACED_CLOSE);
            nodes.pop();
        }
        else
        {
            if(check(Terminals.NAME))
            {
                if(!tryStep(Parcer::call))
                {
                    addNode(new VarNameNode(current().getValue()));
                    step();
                }
            }
            else const_value();
        }

        if(stepIF(Terminals.MATH_OP))
        {
            addNode(new MathOperationNode(tokens.get(index-1).getValue()));
            value();
        }
    }

    // DIGIT | DIGIT_NATURAL | BOOLEAN
    private void const_value() throws BuildExeption
    {
        if(	check(Terminals.DIGIT) ||
                check(Terminals.DIGIT_NATURAL)||
                check(Terminals.BOOLEAN))
        {
            addNode(new ConstantNode(current().getValue(), current().getLexeme().getType()));
            step();
        }
        else
            throw new BuildExeption("Неверный тип, ожидалось число или логическое значение");
    }

    private void line_end() throws BuildExeption
    {
        checkAndStep(Terminals.LINE_END);
    }


    // Не лезь, убьет

    @FunctionalInterface
    interface ParcerFunction {
        void invoke(Parcer self) throws BuildExeption;
    }

    private boolean tryStep(ParcerFunction method)
    {
        int startIndex = index;
        int nodesIndex = nodes.size();
        int nodesChildIndex = nodes.lastElement() instanceof BodyNode ? ((BodyNode) nodes.lastElement()).getSize() : 0;

        try
        {
            method.invoke(this);
        }
        catch(BuildExeption exeption)
        {
            //backtrack...

            errors.add(new Pair<>(index, exeption.getMessage()));
            index = startIndex;

            while(nodes.size() > nodesIndex)
                nodes.pop();

            if(nodes.lastElement() instanceof BodyNode)
                ((BodyNode) nodes.lastElement()).backtrack(nodesChildIndex);

            return false;
        }
        return true;
    }

    private boolean stepIF(Terminals type) throws BuildExeption
    {
        if(check(type))
        {
            step();
            return true;
        }
        return false;
    }

    private boolean check(Terminals type)throws BuildExeption
    {
        return current().getLexeme().getType() == type;
    }

    private String checkAndStep(Terminals type) throws BuildExeption
    {
        Token token = current();

        if(token.getLexeme().getType() != type)
            throw new BuildExeption("Неверная синтаксическая конструкция, ожидалось " + type.toString());

        step();
        return token.getValue();
    }

    private Token current() throws BuildExeption
    {
        if(index >= tokens.size())
            throw new BuildExeption("Непредвиденный конец инструкций");

        return tokens.get(index);
    }

    private void step()
    {
        ++index;
    }

    private void addNode(Node node)
    {
        ((BodyNode)nodes.lastElement()).addChild(node);
    }

    private void addAndPushNode(Node node)
    {
        ((BodyNode)nodes.lastElement()).addChild(node);
        nodes.push(node);
    }
}

    public void Parse(List<Token> tokens) throws SyntaxBuilderException{
        if(tokens.isEmpty()) {
            throw new SyntaxBuilderException("Tokens list is empty", "None", 0);
        }
        this.tokens = tokens;
        currentTokenIndex = 0;

        while(currentTokenIndex < tokens.size())
        {
            currentToken = tokens.get(currentTokenIndex);
            Step(currentToken);

            currentTokenIndex++;
        }
    }
    private boolean Define_Var_Simple()
    {
        if(IsEqualTerm(VAR_TYPE)){
            if(IsEqualTerm(NAME)){
                return true;
            }
            currentTokenIndex--;
        }
        return false;
    }
    private boolean IsEqualTerm(Term term)
    {
        if(currentTokenIndex + 1 >= tokens.size())
            return false;
        else if(tokens.get(currentTokenIndex + 1).GetLexeme().GetTerminal() == term){
            currentTokenIndex++;
            return true;
        }
        else
            return false;
    }
    private boolean Const_Value()
    {
        if(currentTokenIndex + 1 >= tokens.size())
            return false;
        switch (tokens.get(currentTokenIndex + 1).GetLexeme().GetTerminal())
        {
            case NATURAL_DIGIT:
                currentTokenIndex++;
                return true;
            case DIGIT:
                currentTokenIndex++;
                return true;
            case BOOLEAN:
                currentTokenIndex++;
                return true;
            default:
                return false;
        }
    }
    private boolean Value(MethodNode method)
    {
        if(currentTokenIndex + 1 >= tokens.size())
            return false;
        else if(Const_Value() || IsEqualTerm(NAME) || Call()){
            method.AddChild();
        }
        else
            return false;
    }
    private boolean Call()
    {
        if(currentTokenIndex + 1 >= tokens.size())
            return false;
        if(IsEqualTerm(NAME))
            if(IsEqualTerm(OPENING_BRACKET)){
                if(Value()) {

                }
            }
    }
    private boolean Define_Var()
    {
        if(Define_Var_Simple()){
            if(currentTokenIndex + 1>= tokens.size())
                return false;
            else if(IsEqualTerm(ASSIGN_OP)){
                if()
            }
            else
                return false;
        }
    }
    private boolean Expr_Body()
    {

    }
    private BodyNode Body()
    {
        if(IsEqualTerm(OPENING_BODY)){
            if()
        }
    }

    private boolean Method() throws SyntaxBuilderException {
        Node method = new MethodNode();
        List<Argument> arguments = new ArrayList<>();
        if(IsEqualTerm(VAR_TYPE)) {
            ((MethodNode) method).SetReturnValue(tokens.get(currentTokenIndex));
            if (IsEqualTerm(Term.NAME)) {
                ((MethodNode) method).SetName(tokens.get(currentTokenIndex).GetMatch());
                if (IsEqualTerm(Term.OPENING_BRACKET))
                    if (Define_Var_Simple()) {
                        arguments.add(new Argument(tokens.get(currentTokenIndex - 1),tokens.get(currentTokenIndex)));
                        while (!IsEqualTerm(CLOSING_BRACKET)) {
                            if(IsEqualTerm(COMMA)) {
                                if(Define_Var_Simple())
                                    arguments.add(new Argument(tokens.get(currentTokenIndex - 1), tokens.get(currentTokenIndex)));
                            }
                            else
                                throw new SyntaxBuilderException("Некорректные аргументы функции, CurrentTokenIndex №" + String.valueOf(currentTokenIndex),method.getClass().getName(),1);
                        }
                        if (Body())
                            return true;
                    }
                    else if(IsEqualTerm(CLOSING_BRACKET)){
                        if(Body())
                            return true;
                    }
            }
        }
        return false;

    }
    private boolean Expr()
    {

    }

    private boolean TryStep(Term term)
    {

    }
    private void Step(Token token)
    {
        TryStep(token.GetLexeme().GetTerminal());
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
        if(tokens.isEmpty() || currentTokenIndex >= tokens.size())
            throw new ParserException("Попытка считать несуществующий токен!");
        return tokens.get(currentTokenIndex);
    }
}

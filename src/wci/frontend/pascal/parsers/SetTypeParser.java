package wci.frontend.pascal.parsers;

import java.util.EnumSet;

import wci.frontend.*;
import wci.frontend.pascal.*;
import wci.intermediate.*;
import wci.intermediate.symtabimpl.*;

import static wci.frontend.pascal.PascalTokenType.*;
import static wci.frontend.pascal.PascalErrorCode.*;
import static wci.intermediate.symtabimpl.SymTabKeyImpl.*;
import static wci.intermediate.typeimpl.TypeFormImpl.SET;
import static wci.intermediate.typeimpl.TypeFormImpl.SUBRANGE;
import static wci.intermediate.typeimpl.TypeFormImpl.ENUMERATION;
import static wci.intermediate.typeimpl.TypeKeyImpl.*;

/**
 * <h1>SetTypeParser</h1>
 *
 * <p>Parse a Pascal array type specification.</p>
 *
 * <p>Copyright (c) 2009 by Team MadCode</p>
 * <p>For Education purposes only.  No warranties.</p>
 */
class SetTypeParser extends TypeSpecificationParser
{
    /**
     * Constructor.
     * @param parent the parent parser.
     */
    protected SetTypeParser(PascalParserTD parent)
    {
        super(parent);
    }

    // Synchronization set for OF.
    private static final EnumSet<PascalTokenType> OF_SET =
        TypeSpecificationParser.TYPE_START_SET.clone();
    static {
        OF_SET.add(OF);
    }
    private static final EnumSet<PascalTokenType> END_SET =
            TypeSpecificationParser.TYPE_START_SET.clone();
        static {
        	END_SET.add(SEMICOLON);
        }

    /**
     * Parse a Pascal array type specification.
     * @param token the current token.
     * @return the array type specification.
     * @throws Exception if an error occurred.
     */
    public TypeSpec parse(Token token)
        throws Exception
    {
        TypeSpec setType = TypeFactory.createType(SET);
        token = nextToken();  // consume SET

        // Synchronize at OF.
        token = synchronize(OF_SET);
        if (token.getType() == OF) {
            token = nextToken();  // consume OF
            switch((PascalTokenType) token.getType()) {
            	case IDENTIFIER:
            		SymTabEntry identifier = symTabStack.lookup(token.getText().toLowerCase());
            		if(identifier != null)
            		{
            			setType.setAttribute(SET_NAMED, identifier.getTypeSpec());
                                setType.setIdentifier(identifier);
            			token = nextToken();  // consume IDENTIFIER
            			return setType;
            		}
            		else
            		{
            			token = synchronize(END_SET);
            			return null;
            		}
            	case INTEGER:
            		setType.setAttribute(SET_UNNAMED, new SubrangeTypeParser(this).parse(token));
                    return setType;
            	case LEFT_PAREN:
            		setType.setAttribute(SET_UNNAMED, new EnumerationTypeParser(this).parse(token));
            		return setType;
            	default:
            		token = synchronize(END_SET);
            		return null;
            }
        }
        else {
            errorHandler.flag(token, MISSING_OF, this);
            return null;
        }
    }
}

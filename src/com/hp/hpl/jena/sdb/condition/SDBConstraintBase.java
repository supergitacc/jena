/*
 * (c) Copyright 2006 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package com.hp.hpl.jena.sdb.condition;

import java.util.Collection;

import com.hp.hpl.jena.query.util.IndentedLineBuffer;
import com.hp.hpl.jena.query.util.Symbol;
import com.hp.hpl.jena.sdb.core.CompileContext;
import com.hp.hpl.jena.sdb.core.Var;
import com.hp.hpl.jena.sdb.core.sqlexpr.SqlExpr;

public abstract class SDBConstraintBase implements SDBConstraint
{
    private Symbol label ;
    
    public SDBConstraintBase(String labelStr)
    {
        label = new Symbol(labelStr) ;
    }
    
    public String getLabel() { return label.toString() ; }
    
    public SqlExpr asSqlExpr(CompileContext cxt)
    {
        // Will need to worry about isBound() and being the right type later 
        return SqlExprGenerator.compile(cxt, this) ;
    }
    
//    public final String asSQL(CompileContext cxt)
//    {
//        IndentedLineBuffer buff = new IndentedLineBuffer() ;
//        SDBConstraintVisitor v = new SQLCondition(buff.getIndentedWriter(),cxt) ;
//        this.visit(v) ;
//        return buff.asString() ;
//    }

    @Override
    final
    public String toString()
    {
          IndentedLineBuffer buff = new IndentedLineBuffer() ;
          SDBConstraintVisitor v = new SDBConstraintText(buff.getIndentedWriter()) ;
          this.visit(v) ;
          return buff.asString() ;
    }
    
    final
    public void varsMentioned(Collection<Var> acc)
    {
        SDBConstraintVisitor visitor = new VarsMentionVisitor(acc) ;
        this.visit(visitor) ;
    }
}

/*
 * (c) Copyright 2006 Hewlett-Packard Development Company, LP
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
/*
 * (c) Copyright 2008 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package com.hp.hpl.jena.tdb.lib;

import static com.hp.hpl.jena.tdb.sys.SystemTDB.SizeOfLong;
import iterator.Iter;
import iterator.Transform;

import java.util.Iterator;

import lib.Bytes;
import lib.ColumnMap;
import lib.Tuple;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;

import com.hp.hpl.jena.sparql.core.Quad;

import com.hp.hpl.jena.tdb.TDBException;
import com.hp.hpl.jena.tdb.base.record.Record;
import com.hp.hpl.jena.tdb.base.record.RecordFactory;
import com.hp.hpl.jena.tdb.nodetable.NodeTable;
import com.hp.hpl.jena.tdb.store.NodeId;

public class TupleLib
{
    public static  Iterator<Tuple<Node>> convertToNodes(final NodeTable nodeTable, Iterator<Tuple<NodeId>> iter)
    {
        Transform<Tuple<NodeId>, Tuple<Node>> action =  new Transform<Tuple<NodeId>, Tuple<Node>>(){
            @Override
            public Tuple<Node> convert(Tuple<NodeId> item)
            {
                return tupleNodes(nodeTable, item) ;
            }} ;
        return Iter.map(iter, action) ;
    }
    
    public static Iterator<Tuple<NodeId>> convertToNodeId(final NodeTable nodeTable, Iterator<Tuple<Node>> iter)
    {
        Transform<Tuple<Node>, Tuple<NodeId>> action =  new Transform<Tuple<Node>, Tuple<NodeId>>(){
            @Override
            public Tuple<NodeId> convert(Tuple<Node> item)
            {
                return tupleNodeIds(nodeTable, item) ;
            }} ;
        return Iter.map(iter, action) ;
    }
    
    //@Deprecated
    //Leave - bypasses extrat step in Tuple<NodeId> -> Tuple<Node> -> Triple
    public static Iterator<Triple> convertToTriples(final NodeTable nodeTable, Iterator<Tuple<NodeId>> iter)
    {
        Transform<Tuple<NodeId>, Triple> action =  new Transform<Tuple<NodeId>, Triple>(){
            @Override
            public Triple convert(Tuple<NodeId> item)
            {
                return triple(nodeTable, item) ;
            }} ;
        return Iter.map(iter, action) ;
    }
    
    //@Deprecated
    public static Iterator<Quad> convertToQuads(final NodeTable nodeTable, Iterator<Tuple<NodeId>> iter)
    {
        Transform<Tuple<NodeId>, Quad> action =  new Transform<Tuple<NodeId>, Quad>(){
            @Override
            public Quad convert(Tuple<NodeId> item)
            {
                return quad(nodeTable, item) ;
            }} ;
        return Iter.map(iter, action) ;
    }
    
    public static Tuple<Node> tupleNodes(NodeTable nodeTable, Tuple<NodeId> ids) 
    {
        int N = ids.size() ;
        Node[] n = new Node[N] ;
        for ( int i = 0 ; i < N ; i++ )
            n[i] = nodeTable.retrieveNodeByNodeId(ids.get(i)) ;
        return new Tuple<Node>(n) ;
    }
    
    public static Tuple<NodeId> tupleNodeIds(NodeTable nodeTable, Tuple<Node> nodes) 
    {
        int N = nodes.size() ;
        NodeId[] n = new NodeId[N] ;
        for ( int i = 0 ; i < N ; i++ )
            n[i] = nodeTable.nodeIdForNode(nodes.get(i)) ;
            
        return new Tuple<NodeId>(n) ;
    }

    @Deprecated
    public static Triple triple(NodeTable nodeTable, NodeId s, NodeId p, NodeId o) 
    {
        Node sNode = nodeTable.retrieveNodeByNodeId(s) ;
        Node pNode = nodeTable.retrieveNodeByNodeId(p) ;
        Node oNode = nodeTable.retrieveNodeByNodeId(o) ;
        return new Triple(sNode, pNode, oNode) ;
    }
    
    @Deprecated
    public static Quad quad(NodeTable nodeTable, NodeId g, NodeId s, NodeId p, NodeId o) 
    {
        Node gNode = nodeTable.retrieveNodeByNodeId(g) ;
        Node sNode = nodeTable.retrieveNodeByNodeId(s) ;
        Node pNode = nodeTable.retrieveNodeByNodeId(p) ;
        Node oNode = nodeTable.retrieveNodeByNodeId(o) ;
        return new Quad(gNode, sNode, pNode, oNode) ;
    }

    //@Deprecated
    private static Triple triple(NodeTable nodeTable, Tuple<NodeId> tuple) 
    {
        if ( tuple.size() != 3 )
            throw new TDBException("Tuple is not of length 3: "+tuple) ;
        return triple(nodeTable, tuple.get(0), tuple.get(1), tuple.get(2)) ;
    }
    
    //@Deprecated
    private static Quad quad(NodeTable nodeTable, Tuple<NodeId> tuple) 
    {
        if ( tuple.size() != 4 )
            throw new TDBException("Tuple is not of length 4: "+tuple) ;
        return quad(nodeTable, tuple.get(0), tuple.get(1), tuple.get(2), tuple.get(3)) ;
    }
    
    // ---- Tuples, Triples and Quads

//    /** Triple to Tuple, not remapped by a ColumnMap. */
//    public static Tuple<NodeId> tuple(Triple t, NodeTable nodeTable)
//    {
//        Node s = t.getSubject() ;
//        Node p = t.getPredicate() ;
//        Node o = t.getObject() ;
//
//        NodeId x = nodeTable.storeNode(s) ;
//        NodeId y = nodeTable.storeNode(p) ;
//        NodeId z = nodeTable.storeNode(o) ;
//        return new Tuple<NodeId>(x, y, z) ;  
//    }
//
//    /** Quad to Tuple, not remapped by a ColumnMap. */
//    public static Tuple<NodeId> tuple(Quad quad, NodeTable nodeTable)
//    {
//        return tuple(quad.getGraph(), quad.getSubject(), quad.getPredicate(), quad.getObject(), nodeTable) ;
//    }
//    
//    /** Quad (as graph node and triple) to Tuple, not remapped by a ColumnMap. */
//    public static Tuple<NodeId> tuple(Node g, Triple t, NodeTable nodeTable)
//    {
//        return tuple(g, t.getSubject(), t.getPredicate(), t.getObject(), nodeTable) ;
//    }
//    
//    public static Tuple<NodeId> tuple(Node g, Node s, Node p, Node o, NodeTable nodeTable)
//    {
//        NodeId gId = nodeTable.storeNode(g) ;
//        NodeId sId = nodeTable.storeNode(s) ;
//        NodeId pId = nodeTable.storeNode(p) ;
//        NodeId oId = nodeTable.storeNode(o) ;
//        
//        return new Tuple<NodeId>(gId, sId, pId, oId) ;  
//    }
    
    // ---- Tuples and Records
    public static Tuple<NodeId> tuple(Record r, ColumnMap cMap)
    {
        int N = r.getKey().length/SizeOfLong ;
        NodeId[] nodeIds = new NodeId[N] ;
        for ( int i = 0 ; i < N ; i++ )
        {
            long x = Bytes.getLong(r.getKey(), i*SizeOfLong) ;
            NodeId id = NodeId.create(x) ;
            int j = i ;
            if ( cMap != null )
                j = cMap.fetchSlotIdx(i) ;
            nodeIds[j] = id ;
        }
        return new Tuple<NodeId>(nodeIds) ;
    }


    public static Record record(RecordFactory factory, Tuple<NodeId> tuple, ColumnMap cMap) 
    {
        byte[] b = new byte[tuple.size()*NodeId.SIZE] ;
        for ( int i = 0 ; i < tuple.size() ; i++ )
        {
            int j = cMap.mapSlotIdx(i) ;
            // i'th Nodeid goes to j'th bytes slot.
            Bytes.setLong(tuple.get(i).getId(), b,j*SizeOfLong) ;
        }
            
        return factory.create(b) ;
    }


    // OLD to go.
    @Deprecated
    public static Record record(RecordFactory factory, NodeId...nodeIds)
    {
        byte[] b = new byte[nodeIds.length*NodeId.SIZE] ;
        for ( int i = 0 ; i < nodeIds.length ; i++ )
            Bytes.setLong(nodeIds[i].getId(), b, i*SizeOfLong) ;  
        return factory.create(b) ;
    }


    // OLD to go.
    @Deprecated
    public static Record record(RecordFactory factory, long...nodeIds)
    {
        byte[] b = new byte[nodeIds.length*NodeId.SIZE] ;
        for ( int i = 0 ; i < nodeIds.length ; i++ )
            Bytes.setLong(nodeIds[i], b, i*SizeOfLong) ;  
        return factory.create(b) ;
    }

}

/*
 * (c) Copyright 2008 Hewlett-Packard Development Company, LP
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
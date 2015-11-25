RiverNe3
========

``@author`` Francesco Serafin (sidereus3), francesco.serafin.3@gmail.com

``@copyright`` GNU Public License v3 AboutHydrology (Riccardo Rigon)

Description
-----------

This component (which is not part of OMS (David et al. 2013) yet) is an
abstract schematization of a river network through a binary tree, in
order to parallel process independet HURs.

Implementation
~~~~~~~~~~~~~~

The structure was thought as more flexible and extendable as possible.
To reach this goal, several patterns were implemented and the structure
is completely decoupled from the type of tree. The three main patterns
are:

-  Composite Pattern;
-  Decorator Pattern;
-  Factory Pattern.

Composite Pattern
^^^^^^^^^^^^^^^^^

The list of nodes of the tree is represented by a
``ConcurrentHashMap<Key, Node>`` to make it parsable by
multi-threadings. No nested calls were implemented. Thus, the *composite
pattern* is similar to a *strategy pattern*, where each type of node
(the already implemented ones are ``GhostNode``, ``Leaf``, ``LocalNode``
and ``Node`` but you can easily add a new one) extends the abstract
class ``Component.java``. However, each node has its own *TreeTraverser*
as in the composite pattern theory.

The connections between nodes is represented by the ``Connections.java``
object which is the abstract class of a *strategy pattern* in order to
allow each user to develop the type of connection required by the tree.
For the *Binary Tree*, the ``BinaryConnections.java`` was implemented.

Each node has its own *ID*, which is stored in the ``Key.java`` object.

Decorator Pattern
^^^^^^^^^^^^^^^^^

The *decorator pattern* was implemented to allow each developer to write
her/his own plain tree (e.g. ``RiverBinaryTree.java`` useful to abstract
a simple river network (H. Wang et al. 2011)), implementing the
interface ``Tree.java``.

Once you have your own plain tree, you can decorat it writing a
*decorator* (e.g. ``Hydrometers.java``), extending the
``BinaryTreeDecorator.java`` abstract class.

Simple Factory - Factory Method Pattern
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

*Factory Patterns* were implemented without having to specify the exact
class of the object that will be created.

Useful links
------------

Developers' documentation
~~~~~~~~~~~~~~~~~~~~~~~~~

On-line documentation available on http://sidereus3.github.io/RiverNe3/

Linkers' documentation
~~~~~~~~~~~~~~~~~~~~~~

Users' documentation
~~~~~~~~~~~~~~~~~~~~

References
----------

.. raw:: html

   <div id="refs" class="references">

.. raw:: html

   <div id="ref-david2013:software">

David, Olaf, JC Ascough Ii, Wes Lloyd, TR Green, KW Rojas, GH Leavesley,
and LR Ahuja. 2013. “A Software Engineering Perspective on Environmental
Modeling Framework Design: The Object Modeling System.” *Environmental
Modelling & Software* 39. Elsevier: 201–13.
http://www.sciencedirect.com/science/article/pii/S1364815212000886.

.. raw:: html

   </div>

.. raw:: html

   <div id="ref-wang2011:common">

Wang, Hao, Xudong Fu, Guangqian Wang, Tiejian Li, and Jie Gao. 2011. “A
Common Parallel Computing Framework for Modeling Hydrological Processes
of River Basins.” *Parallel Computing* 37 (6). Elsevier: 302–15.

.. raw:: html

   </div>

.. raw:: html

   </div>

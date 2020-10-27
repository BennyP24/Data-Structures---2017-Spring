// SUBMIT
public class BTree implements BTreeInterface {

	// ///////////////////BEGIN DO NOT CHANGE ///////////////////
	// ///////////////////BEGIN DO NOT CHANGE ///////////////////
	// ///////////////////BEGIN DO NOT CHANGE ///////////////////
	private BNode root;
	private final int t;

	/**
	 * Construct an empty tree.
	 */
	public BTree(int t) { //
		this.t = t;
		this.root = null;
	}

	// For testing purposes.
	public BTree(int t, BNode root) {
		this.t = t;
		this.root = root;
	}

	@Override
	public BNode getRoot() {
		return root;
	}

	@Override
	public int getT() {
		return t;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((root == null) ? 0 : root.hashCode());
		result = prime * result + t;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BTree other = (BTree) obj;
		if (root == null) {
			if (other.root != null)
				return false;
		} else if (!root.equals(other.root))
			return false;
		if (t != other.t)
			return false;
		return true;
	}
	
	// ///////////////////DO NOT CHANGE END///////////////////
	// ///////////////////DO NOT CHANGE END///////////////////
	// ///////////////////DO NOT CHANGE END///////////////////


	@Override
	public Block search(int key) {
		if(root == null)
			return null;
		else{
			return root.search(key);
		}
	}

	@Override
	public void insert(Block b) {
		BNode oldRoot = this.root;
		//check if root exist
		if( root == null) {
			//create new root
			root = new BNode(t, b);
		}
		//check if root is full
		else if (oldRoot.isFull()) {
			//create a new node
			BNode newRoot = new BNode(t, false, 0);
			//set the old root to be its' child
			newRoot.getChildrenList().add(0, oldRoot);
			//split the new child (the old root) to 2 children with t-1 blocks
			newRoot.splitChild(0);
			//after the split, insert the block to the correct node
			newRoot.insertNonFull(b);
			//set the new root to be the root
			root = newRoot;
		}
		else
			//if the root isn't full, insert the block to the correct node
			oldRoot.insertNonFull(b);
 	}



	@Override
	public void delete(int key) {
		if (root != null){
			 root.delete(key);
			 if(root.getNumOfBlocks() == 0){
			 	if(root.getChildrenList().size() == 0){
					root = null;
				}
			 	else{
			 		root = root.getChildAt(0);
				}
			 }
		}

	}

	@Override
	public MerkleBNode createMBT() {
		if ( root!=null){
			return root.createHashNode();
		}

		return null;
	}


}

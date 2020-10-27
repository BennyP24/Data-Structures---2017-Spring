import java.util.ArrayList;

//SUBMIT
public class BNode implements BNodeInterface {

	// ///////////////////BEGIN DO NOT CHANGE ///////////////////
	// ///////////////////BEGIN DO NOT CHANGE ///////////////////
	// ///////////////////BEGIN DO NOT CHANGE ///////////////////
	private final int t;
	private int numOfBlocks;
	private boolean isLeaf;
	private ArrayList<Block> blocksList;
	private ArrayList<BNode> childrenList;

	/**
	 * Constructor for creating a node with a single child.<br>
	 * Useful for creating a new root.
	 */
	public BNode(int t, BNode firstChild) {
		this(t, false, 0);
		this.childrenList.add(firstChild);
	}

	/**
	 * Constructor for creating a <b>leaf</b> node with a single block.
	 */
	public BNode(int t, Block firstBlock) {
		this(t, true, 1);
		this.blocksList.add(firstBlock);
	}

	public BNode(int t, boolean isLeaf, int numOfBlocks) {
		this.t = t;
		this.isLeaf = isLeaf;
		this.numOfBlocks = numOfBlocks;
		this.blocksList = new ArrayList<Block>();
		this.childrenList = new ArrayList<BNode>();
	}

	// For testing purposes.
	public BNode(int t, int numOfBlocks, boolean isLeaf,
			ArrayList<Block> blocksList, ArrayList<BNode> childrenList) {
		this.t = t;
		this.numOfBlocks = numOfBlocks;
		this.isLeaf = isLeaf;
		this.blocksList = blocksList;
		this.childrenList = childrenList;
	}

	@Override
	public int getT() {
		return t;
	}

	@Override
	public int getNumOfBlocks() {
		return numOfBlocks;
	}

	@Override
	public boolean isLeaf() {
		return isLeaf;
	}

	@Override
	public ArrayList<Block> getBlocksList() {
		return blocksList;
	}

	@Override
	public ArrayList<BNode> getChildrenList() {
		return childrenList;
	}

	@Override
	public boolean isFull() {
		return numOfBlocks == 2 * t - 1;
	}

	@Override
	public boolean isMinSize() {
		return numOfBlocks == t - 1;
	}
	
	@Override
	public boolean isEmpty() {
		return numOfBlocks == 0;
	}
	
	@Override
	public int getBlockKeyAt(int indx) {
		return blocksList.get(indx).getKey();
	}
	
	@Override
	public Block getBlockAt(int indx) {
		return blocksList.get(indx);
	}

	@Override
	public BNode getChildAt(int indx) {
		return childrenList.get(indx);
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((blocksList == null) ? 0 : blocksList.hashCode());
		result = prime * result
				+ ((childrenList == null) ? 0 : childrenList.hashCode());
		result = prime * result + (isLeaf ? 1231 : 1237);
		result = prime * result + numOfBlocks;
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
		BNode other = (BNode) obj;
		if (blocksList == null) {
			if (other.blocksList != null)
				return false;
		} else if (!blocksList.equals(other.blocksList))
			return false;
		if (childrenList == null) {
			if (other.childrenList != null)
				return false;
		} else if (!childrenList.equals(other.childrenList))
			return false;
		if (isLeaf != other.isLeaf)
			return false;
		if (numOfBlocks != other.numOfBlocks)
			return false;
		if (t != other.t)
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "BNode [t=" + t + ", numOfBlocks=" + numOfBlocks + ", isLeaf="
				+ isLeaf + ", blocksList=" + blocksList + ", childrenList="
				+ childrenList + "]";
	}

	// ///////////////////DO NOT CHANGE END///////////////////
	// ///////////////////DO NOT CHANGE END///////////////////
	// ///////////////////DO NOT CHANGE END///////////////////
	
	
	
	@Override
	public Block search(int key) {
		if(key < 0){
			throw new IndexOutOfBoundsException();
		}
		int i = 0;
		while(i < getNumOfBlocks() && key > getBlockKeyAt(i)){
			i++;
		}
		if(i < getNumOfBlocks() && key == getBlockKeyAt(i)){
			return getBlockAt(i);
		}
		else if (isLeaf){
			return null;
		}
		else{
			return getChildAt(i).search(key);
		}
	}

	@Override
	public void insertNonFull(Block d) {
		int i = numOfBlocks;
		//check if this node has children
		if (isLeaf()) {
			//find the location of new key to be inserted and move all greater keys to one index ahead
			while (i >= 1 && d.getKey() < getBlockKeyAt(i - 1)) {
				i--;
			}
			//insert the new block and increase the number of blocks
			blocksList.add(i, d);
			numOfBlocks = numOfBlocks + 1;
		} else {
			//find the child which is going to have the new block
			while (i >= 1 && d.getKey() < getBlockKeyAt(i - 1)) {
				i--;
			}
			//check if the found child is full
			if (getChildAt(i).isFull()) {
				//if it is full- split it
				splitChild(i);
				//check in which of the two children the block is going to be inserted
				if (!blocksList.isEmpty())
					if (d.getKey() > getBlockKeyAt(i))
						i++;
			}
			//insert the block to the correct node
			getChildAt(i).insertNonFull(d);
		}
	}

	@Override
	public void delete(int key) {
		int index = binarySearch(key);
		if(isLeaf()) {
			if (index != numOfBlocks) {
				if (getBlockKeyAt(index) == key) {
					blocksList.remove(index);
					numOfBlocks = numOfBlocks - 1;
				}
			}
		}
		else{
			if (index != numOfBlocks) {

				// the key is in this node
				if (getBlockKeyAt(index) == key) {

					// check for predeseccor
					if(childrenList.get(index).numOfBlocks >= t){
						Block pred = childrenList.get(index).getMaxKeyBlock();
						blocksList.set(index, pred);
						childrenList.get(index).delete(pred.getKey());
					}//check for successor
					else if( childrenList.get(index + 1).numOfBlocks >=t){
						Block succ = childrenList.get(index + 1).getMinKeyBlock();
						blocksList.set(index, succ);
						childrenList.get(index + 1).delete(succ.getKey());
					}
					else{
						mergeChildWithSibling(index + 1);
						childrenList.get(index).delete(key);
					}
				}
			}
			BNode child = getChildAt(index);
			if(child.getNumOfBlocks() < t){
				shiftOrMergeChildIfNeeded(index);
			}
			child.delete(key);
		}
	}

	@Override
	public MerkleBNode createHashNode() {
		ArrayList<byte[]> blocksData = new ArrayList<byte[]>();
		if (isLeaf()){
			for (int i = 0; i <numOfBlocks ; i++) {
				blocksData.add(getBlockAt(i).getData());
			}
			return new MerkleBNode(HashUtils.sha1Hash(blocksData));
		}
		else{
			ArrayList <MerkleBNode> childrenblocksData = new ArrayList<MerkleBNode>();
			for (int i = 0; i < childrenList.size(); i++) {
				childrenblocksData.add(childrenList.get(i).createHashNode());
			}

			for (int i = 0; i < numOfBlocks; i++) {
				blocksData.add(childrenblocksData.get(i).getHashValue());
				blocksData.add(getBlockAt(i).getData());
			}

			blocksData.add(childrenblocksData.get(numOfBlocks).getHashValue());

			return  new MerkleBNode(HashUtils.sha1Hash(blocksData),childrenblocksData);

		}

	}

	//---helping methods---

	public void splitChild(int childIndex){
		//y is the node that we need to split
		BNode y = getChildAt(childIndex);
		//create a new node which is going to store (t-1) blocks of y
		BNode z = new BNode(t, y.isLeaf, t-1);
		//copy the last (t-1) blocks of y to z
		for (int i = 0; i < t-1; i++){
			z.blocksList.add(y.getBlockAt(i+t));
		}
		//check if y has children
		if (!y.isLeaf()) {
			//copy the last t children of y to z
			for (int i = 0; i < t; i++) {
				z.childrenList.add(y.getChildAt(i + t));
			}
			y.getChildrenList().subList(t,y.getChildrenList().size()).clear();
		}

		//add z to the roots' children list
		childrenList.add(childIndex + 1, z);
		//add the block of y with index t to the root
		blocksList.add(childIndex, y.getBlockAt(t-1));
		//increase the number of blocks in the root
		numOfBlocks = numOfBlocks + 1;
		//remove from y the blocks from index t to the end of the list
		y.blocksList.subList(t-1, y.blocksList.size()).clear();
		//reduce the number of blocks in y
		y.numOfBlocks = t-1;
	}

	//DELETE helping methods//

	//True iff the child node at childIndx-1 exists and has more than t-1 blocks.
	private boolean childHasNonMinimalLeftSibling(int childIndx) {
		if (!rangeCheckForChild(childIndx - 1)){
			return false;
		}
		BNode leftSibling = getChildAt(childIndx-1);
		return (leftSibling.blocksList.size() > t-1);
	}

	 // True iff the child node at childIndx+1 exists and has more than t-1 blocks.
	 private boolean childHasNonMinimalRightSibling(int childIndx){
		 if (!rangeCheckForChild(childIndx + 1)){
			 return false;
		 }
		BNode rightSibling = getChildAt(childIndx+1);
		return (rightSibling.blocksList.size() > t-1);
	}

	// Verifies the child node at childIndx has at least t blocks.
	// If necessary a shift or merge is performed.
	private void shiftOrMergeChildIfNeeded(int childIndx) {
		if(childHasNonMinimalLeftSibling(childIndx)){
			shiftFromLeftSibling(childIndx);
		}
		else if(childHasNonMinimalRightSibling(childIndx)){
			shiftFromRightSibling(childIndx);
		}
		else{
			mergeChildWithSibling(childIndx);
		}
	}

	// Add additional block to the child node at childIndx, by shifting from left sibling.
	private void shiftFromLeftSibling ( int childIndx) {
		BNode currentChild = getChildAt(childIndx);
		BNode leftSibling = getChildAt(childIndx - 1);
		currentChild.blocksList.add(0, getBlockAt(childIndx - 1));
		currentChild.numOfBlocks = currentChild.numOfBlocks + 1;
		blocksList.set(childIndx - 1, leftSibling.getBlockAt(leftSibling.numOfBlocks-1));
		leftSibling.blocksList.remove(leftSibling.numOfBlocks-1);
		leftSibling.numOfBlocks = leftSibling.numOfBlocks - 1;
		if(!currentChild.isLeaf()){
			currentChild.childrenList.add(0, leftSibling.childrenList.get(leftSibling.numOfBlocks + 1));
			leftSibling.childrenList.remove(leftSibling.numOfBlocks + 1);
		}
	}

	// Add additional block to the child node at childIndx, by shifting from right sibling.
	private void shiftFromRightSibling(int childIndx) {
		BNode currentChild = getChildAt(childIndx);
		BNode rightSibling = getChildAt(childIndx + 1);
		currentChild.blocksList.add(getBlockAt(childIndx));
		currentChild.numOfBlocks = currentChild.numOfBlocks + 1;
		blocksList.set(childIndx, rightSibling.getBlockAt(0));
		rightSibling.blocksList.remove(0);
		rightSibling.numOfBlocks = rightSibling.numOfBlocks - 1;
		if(!currentChild.isLeaf()){
			currentChild.childrenList.add(rightSibling.childrenList.get(0));
			rightSibling.childrenList.remove(0);
		}
	}

	// Merges the child node at childIndx with its left or right sibling.
	private void mergeChildWithSibling(int childIndx) {
		if (rangeCheckForChild(childIndx - 1)) {
			mergeWithLeftSibling(childIndx);
		}
		else {
			mergeWithRightSibling(childIndx);
		}
	}

	//Merges the child node at childIndx with its left sibling.
	//The left sibling node is removed.
	private void mergeWithLeftSibling(int childIndx){
		BNode v = getChildAt(childIndx);
	 	BNode left = getChildAt(childIndx - 1);
		v.blocksList.add(0, getBlockAt(childIndx - 1));
		v.blocksList.addAll(0, left.blocksList);
		v.numOfBlocks = v.numOfBlocks + left.numOfBlocks + 1;
		childrenList.remove(childIndx-1);
		blocksList.remove(childIndx-1);
		numOfBlocks = numOfBlocks - 1;
		if(!v.isLeaf()){
			v.childrenList.addAll(0, left.childrenList);
		}
	}

	// Merges the child node at childIndx with its right sibling.
	// The right sibling node is removed.
	private void mergeWithRightSibling(int childIndx){
		BNode v = getChildAt(childIndx);
		BNode right = getChildAt(childIndx + 1);
		v.blocksList.add(getBlockAt(childIndx));
		v.blocksList.addAll(right.blocksList);
		v.numOfBlocks = v.numOfBlocks + right.numOfBlocks + 1;
		childrenList.remove(childIndx+1);
		blocksList.remove(childIndx);
		numOfBlocks = numOfBlocks - 1;
		if(!v.isLeaf()){
			v.childrenList.addAll(right.childrenList);
		}
	}

	// Finds and returns the block with the min key in the subtree.
	private Block getMinKeyBlock() {
		if (!isLeaf()){
		   return getChildAt(0).getMinKeyBlock();
        }
        return getBlockAt(0);
	}

	//Finds and returns the block with the max key in the subtree.
	private Block getMaxKeyBlock() {
        if (!isLeaf()){
           return getChildAt(numOfBlocks).getMaxKeyBlock();
        }
        return getBlockAt(numOfBlocks-1);
    }

    private int binarySearch (int key){
		int min = 0;
		int max = numOfBlocks - 1;
		while(min != max && min < max){
			int index = ( min + max )/ 2;
			if( getBlockKeyAt(index) > key){
				if (index > 0){
					max = index - 1;
				}else{
					max = 0;
				}
			}
			else if( getBlockKeyAt(index) == key){
				return index;
			}
			else{
				min = index + 1;
			}
		}
		if (getBlockKeyAt(min) < key) {
			return (min + 1);
		}else if( (min == getNumOfBlocks() - 1) && (getBlockKeyAt(min) < key)) {
			return numOfBlocks;
		}
		return min;
	}


	private boolean rangeCheckForChild(int index){
		return (index >= 0 && index <= numOfBlocks);
	}
}


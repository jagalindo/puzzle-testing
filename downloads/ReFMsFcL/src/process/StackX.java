package process;

public class StackX {
	
	private final int SIZE = 20;
	private Concept[] st;
	private int top;

	// ------------------------------------------------------------
	public StackX(){            // constructor
		st = new Concept[SIZE]; // make array
		top = -1;
	}

	// ------------------------------------------------------------
	public void push(Concept j){ // put item on stack
		st[++top] = j;
	}

	// ------------------------------------------------------------
	public Concept pop(){ // take item off stack
		return st[top--];
	}

	// ------------------------------------------------------------
	public Concept peek(){ // peek at top of stack
		return st[top];
	}

	// ------------------------------------------------------------
	public boolean isEmpty(){ // true if nothing on stack
		return (top == -1);
	}
	// ------------------------------------------------------------

} // end class StackX


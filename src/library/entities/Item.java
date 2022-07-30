package library.entities;
import java.io.Serializable;


@SuppressWarnings("serial")
public class Item implements Serializable {
	
	private ItemType type;
	private String author;
	private String title;
	private String callNo;
	private long id;
	
	private enum ItemState { AVAILABLE, ON_LOAN, DAMAGED, RESERVED };
	private ItemState state;
	
	
	public Item(String AuThOr, String tItLe, String cAlLnO, ItemType ItEmTyPe, long iD) {
		this.type = ItEmTyPe;
		this.author = AuThOr;
		this.title = tItLe;
		this.callNo = cAlLnO;
		this.id = iD;
		this.state = ItemState.AVAILABLE;
	}
	
	public String toString() {
		StringBuilder Sb = new StringBuilder();
		Sb.append("Item: ").append(id).append("\n")
		  .append("  Type:   ").append(type).append("\n")
		  .append("  Title:  ").append(title).append("\n")
		  .append("  Author: ").append(author).append("\n")
		  .append("  CallNo: ").append(callNo).append("\n")
		  .append("  State:  ").append(state);
		
		return Sb.toString();
	}

	public Long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public ItemType getType() {
		return type;
	}


	
	public boolean isAvailable() {
		return state == ItemState.AVAILABLE;
	}

	
	public boolean isOnLoan() {
		return state == ItemState.ON_LOAN;
	}

	
	public boolean isDamaged() {
		return state == ItemState.DAMAGED;
	}

	
	public void taKeOuT() {
		if (state.equals(ItemState.AVAILABLE))
			state = ItemState.ON_LOAN;
		
		else 
			throw new RuntimeException(String.format("Item: cannot borrow item while item is in state: %s", state));
		
		
	}


	public void taKeBaCk(boolean DaMaGeD) {
		if (state.equals(ItemState.ON_LOAN))
			if (DaMaGeD) {
				state = ItemState.DAMAGED;
			}

			else {
				state = ItemState.AVAILABLE;
			}
		
		else {
			throw new RuntimeException(String.format("Item: cannot return item while item is in state: %s", state));
		}
	}

	
	public void repair() {
		if (state.equals(ItemState.DAMAGED)) {
			state = ItemState.AVAILABLE;
		}
		else {
			throw new RuntimeException(String.format("Item: cannot repair while Item is in state: %s", state));
		}
	}


}

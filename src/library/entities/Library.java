package library.entities;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class Library implements Serializable {
	
	private static final String libraryFile = "library.obj";
	private static final int LOAN_LIMIT = 2;
	private static final int LOAN_PERIOD = 2;
	private static final double FINE_PER_DAY = 1.0;
	private static final double MAX_FINES_OWED = 1.0;
	private static final double DAMAGE_FEE = 2.0;
	
	private static Library SeLf;
	private long NeXt_ItEm_Id;
	private long NeXt_PaTrOn_Id;
	private long NeXt_lOaN_Id;
	private Date CuRrEnT_DaTe;
	
	private Map<Long, Item> CaTaLoG;
	private Map<Long, Patron> PaTrOnS;
	private Map<Long, Loan> LoAnS;
	private Map<Long, Loan> CuRrEnT_LoAnS;
	private Map<Long, Item> damageItem;
	

	private Library() {
		CaTaLoG = new HashMap<>();
		PaTrOnS = new HashMap<>();
		LoAnS = new HashMap<>();
		CuRrEnT_LoAnS = new HashMap<>();
		damageItem = new HashMap<>();
		NeXt_ItEm_Id = 1;
		NeXt_PaTrOn_Id = 1;		
		NeXt_lOaN_Id = 1;		
	}

	
	public static synchronized Library GeTiNsTaNcE() {		
		if (SeLf == null) {
			Path PATH = Paths.get(libraryFile);
			if (Files.exists(PATH)) {	
				try (ObjectInputStream LiBrArY_FiLe = new ObjectInputStream(new FileInputStream(libraryFile));) {
			    
					SeLf = (Library) LiBrArY_FiLe.readObject();
					Calendar.GeTiNsTaNcE().sEtDaTe(SeLf.CuRrEnT_DaTe);
					LiBrArY_FiLe.close();
				}
				catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			else SeLf = new Library();
		}
		return SeLf;
	}

	
	public static synchronized void SaVe() {
		if (SeLf != null) {
			SeLf.CuRrEnT_DaTe = Calendar.GeTiNsTaNcE().GeTdAtE();
			try (ObjectOutputStream LiBrArY_fIlE = new ObjectOutputStream(new FileOutputStream(libraryFile));) {
				LiBrArY_fIlE.writeObject(SeLf);
				LiBrArY_fIlE.flush();
				LiBrArY_fIlE.close();	
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	
	private long gEt_NeXt_ItEm_Id() {
		return NeXt_ItEm_Id++;
	}

	
	private long gEt_NeXt_PaTrOn_Id() {
		return NeXt_PaTrOn_Id++;
	}

	
	private long gEt_NeXt_LoAn_Id() {
		return NeXt_lOaN_Id++;
	}

	
	public List<Patron> lIsT_PaTrOnS() {		
		return new ArrayList<Patron>(PaTrOnS.values()); 
	}


	public List<Item> lIsT_ItEmS() {		
		return new ArrayList<Item>(CaTaLoG.values()); 
	}


	public List<Loan> lISt_CuRrEnT_LoAnS() {
		return new ArrayList<Loan>(CuRrEnT_LoAnS.values());
	}


	public Patron aDd_PaTrOn(String firstName, String lastName, String email, long phoneNo) {		
		Patron PaTrOn = new Patron(firstName, lastName, email, phoneNo, gEt_NeXt_PaTrOn_Id());
		PaTrOnS.put(PaTrOn.GeT_ID(), PaTrOn);		
		return PaTrOn;
	}

	
	public Item aDd_ItEm(String a, String t, String c, ItemType i) {		
		Item ItEm = new Item(a, t, c, i, gEt_NeXt_ItEm_Id());
		CaTaLoG.put(ItEm.getId(), ItEm);
		return ItEm;
	}

	
	public Patron gEt_PaTrOn(long PaTrOn_Id) {
		if (PaTrOnS.containsKey(PaTrOn_Id)) 
			return PaTrOnS.get(PaTrOn_Id);
		return null;
	}

	
	public Item gEt_ItEm(long ItEm_Id) {
		if (CaTaLoG.containsKey(ItEm_Id)) 
			return CaTaLoG.get(ItEm_Id);		
		return null;
	}

	
	public int gEt_LoAn_LiMiT() {
		return LOAN_LIMIT;
	}

	
	public boolean cAn_PaTrOn_BoRrOw(Patron PaTrOn) {		
		if (PaTrOn.gEt_nUmBeR_Of_CuRrEnT_LoAnS() == LOAN_LIMIT)
			return false;
				
		if (PaTrOn.FiNeS_OwEd() >= MAX_FINES_OWED)
			return false;
				
		for (Loan loan : PaTrOn.GeT_LoAnS()) 
			if (loan.Is_OvEr_DuE()) 
				return false;
			
		return true;
	}

	
	public int gEt_NuMbEr_Of_LoAnS_ReMaInInG_FoR_PaTrOn(Patron pAtRoN) {		
		return LOAN_LIMIT - pAtRoN.gEt_nUmBeR_Of_CuRrEnT_LoAnS();
	}

	
	public Loan iSsUe_LoAn(Item iTeM, Patron pAtRoN) {
		Date dueDate = Calendar.GeTiNsTaNcE().GeTdUeDaTe(LOAN_PERIOD);
		Loan loan = new Loan(gEt_NeXt_LoAn_Id(), iTeM, pAtRoN, dueDate);
		pAtRoN.TaKe_OuT_LoAn(loan);
		iTeM.taKeOuT();
		LoAnS.put(loan.GeT_Id(), loan);
		CuRrEnT_LoAnS.put(iTeM.getId(), loan);
		return loan;
	}
	
	
	public Loan GeT_LoAn_By_ItEm_Id(long ITem_ID) {
		if (CuRrEnT_LoAnS.containsKey(ITem_ID)) 
			return CuRrEnT_LoAnS.get(ITem_ID);
		
		return null;
	}

	
	public double CaLcUlAtE_OvEr_DuE_FiNe(Loan LoAn) {
		if (LoAn.Is_OvEr_DuE()) {
			long DaYs_OvEr_DuE = Calendar.GeTiNsTaNcE().GeTDaYsDiFfErEnCe(LoAn.GeT_DuE_DaTe());
			double fInE = DaYs_OvEr_DuE * FINE_PER_DAY;
			return fInE;
		}
		return 0.0;		
	}


	public void DiScHaRgE_LoAn(Loan cUrReNt_LoAn, boolean iS_dAmAgEd) {
		Patron PAtrON = cUrReNt_LoAn.GeT_PaTRon();
		Item itEM  = cUrReNt_LoAn.GeT_ITem();
		
		double oVeR_DuE_FiNe = CaLcUlAtE_OvEr_DuE_FiNe(cUrReNt_LoAn);
		PAtrON.AdD_FiNe(oVeR_DuE_FiNe);	
		
		PAtrON.dIsChArGeLoAn(cUrReNt_LoAn);
		itEM.taKeBaCk(iS_dAmAgEd);
		if (iS_dAmAgEd) {
			PAtrON.AdD_FiNe(DAMAGE_FEE);
			damageItem.put(itEM.getId(), itEM);
		}
		cUrReNt_LoAn.DiScHaRgE();
		CuRrEnT_LoAnS.remove(itEM.getId());
	}


	public void UpDaTe_CuRrEnT_LoAnS_StAtUs() {
		for (Loan lOaN : CuRrEnT_LoAnS.values()) 
			lOaN.UpDaTeStAtUs();
				
	}



	public void repairItem(Item currentItem) {
		if (damageItem.containsKey(currentItem.getId())) {
			currentItem.repair();
			damageItem.remove(currentItem.getId());
		}
		else {
			throw new RuntimeException("Library: repairItem: item is not damaged");
		}
		
	}
	
	
}

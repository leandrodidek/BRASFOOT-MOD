package mod.brasfoot;

public class TestVerifyAll {
    public static void main(String[] args) throws Exception {
        System.out.println("Testing Class Loading...");
        Class<?> c1 = Class.forName("a.eg");
        System.out.println("Loaded: " + c1.getName());
        Class<?> c2 = Class.forName("a.dV");
        System.out.println("Loaded: " + c2.getName());
        Class<?> c3 = Class.forName("a.bz");
        System.out.println("Loaded: " + c3.getName());
        Class<?> c4 = Class.forName("a.cD");
        System.out.println("Loaded: " + c4.getName());
        Class<?> c5 = Class.forName("a.fB");
        System.out.println("Loaded: " + c5.getName());
        Class<?> c6 = Class.forName("mod.brasfoot.VarOptionsHelper");
        System.out.println("Loaded: " + c6.getName());
        Class<?> c7 = Class.forName("mod.brasfoot.VarManager");
        System.out.println("Loaded: " + c7.getName() + " (Automatico=" + VarManager.isVarAutomatico() + ")");
        Class<?> c8 = Class.forName("mod.brasfoot.VarDialog");
        System.out.println("Loaded: " + c8.getName());
        int subObsceno = c8.getField("SUB_CONDUTA_OBSCENA").getInt(null);
        System.out.println("SUB_CONDUTA_OBSCENA = " + subObsceno);

        // Testa instanciação do diálogo do VAR para Gesto Obsceno
        javax.swing.JDialog dummyDlg = new javax.swing.JDialog();
        VarDialog dlg = new VarDialog(dummyDlg, VarDialog.EVENT_VERMELHO, VarDialog.RESULT_VERMELHO_MANTIDO,
            VarDialog.SUB_CONDUTA_OBSCENA, "Flamengo", "Gabigol", 74, null, null);
        System.out.println("VarDialog instanciado com sucesso para Gesto Obsceno!");

        System.out.println("ALL CLASSES AND OBSCENE GESTURE VAR VERIFIED WITH 0 ERRORS!");
    }
}

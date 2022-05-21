import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MainFile{
    public static void main(String[] args) {
        Scanner sc1 = new Scanner(System.in);
        System.out.print("Masukkan pattern : ");
        String input1 = sc1.nextLine();

        sc1.reset();

        sc1 = new Scanner(System.in);
        System.out.print("Masukkan problem : ");
        String input2 = sc1.nextLine();
        sc1.close();

        String[] patternstr = input1.split(" ");
        List<Double> patternint = new ArrayList<>();
        for (String str : patternstr){
            patternint.add(Double.parseDouble(str));
        }
        MainFile.printList(patternint);

        String[] problemstr = input2.split(" ");
        List<String> problem = new ArrayList<>();
        for (String str : problemstr){
            problem.add(str);
        }
        MainFile.printList(problem);

        FiguralPuzzleSolver<Double> fps = new FiguralPuzzleSolver<>(patternint, problem);
        fps.solve();

        int k = 0;
        for (ProbableAnswer p : fps.finalresult){
            System.out.println("Urutan " + (k + 1) + " : " + p.problemseq.toString());
            System.out.println("Operasi : " + p.operator.toString());
            System.out.println("Hasil : " + p.value);
        }
    }

    static void printList(List<? extends Object> l){
        for (int i = 0; i < l.size(); i++){
            System.out.println(l.get(i));
        }
        System.out.println("List size : " + l.size());
        System.out.println("\n");
    }
}

class ProbableAnswer{
    List<Double> problemseq = new ArrayList<>(); 
    List<String> operator = new ArrayList<>();
    double value;
}

class FiguralPuzzleSolver<T extends Number>{
    List<T> pattern;
    List<String> problem;
    List<String> operatorlist;

    T ptrtarget;

    List<List<T>> patpermute;
    List<List<String>> oprtpermute;

    Map<List<T>, List<String>> eligiblepattern;
    Map<List<Integer>, List<String>> listofsequence;

    List<ProbableAnswer> finalresult;

    public FiguralPuzzleSolver(List<T> pt, List<String>pb){
        this.pattern = pt;
        this.problem = pb;
        this.operatorlist = new ArrayList<>();

        this.operatorlist.add("+");
        this.operatorlist.add("-");
        this.operatorlist.add("*");
        this.operatorlist.add("/");
        this.oprtpermute = new ArrayList<>();
        this.patpermute = new ArrayList<>();
        this.eligiblepattern = new HashMap<>();
        this.listofsequence = new HashMap<>();
        this.finalresult = new ArrayList<>();

        this.permuteoperator();
    }

    /* class main */
    public void solve(){
        int targetidx = this.problem.indexOf("x");
        this.ptrtarget = this.pattern.get(targetidx);
        this.pattern.remove(targetidx);

        permute(this.pattern, 0, this.pattern.size() - 1);
        System.out.println(this.patpermute.toString());
        System.out.println("-----------------\n");

        for (List<String> lstr : this.oprtpermute){
            for (List<T> lint : this.patpermute){
                this.applypatternoperator(lint, lstr);
            }
        }
        System.out.println(this.eligiblepattern.toString());
        this.listofsequence = this.permutesequence();
        System.out.println(this.listofsequence.toString());

        System.out.println("-----------------\n");

        this.problem.remove(targetidx);

        // Konversi problem list menjadi tipe double
        List<Double> problemconverted = new ArrayList<>();
        for (String prob : this.problem){
            problemconverted.add(Double.parseDouble(prob));
        }
        this.applytoproblem(problemconverted);
    }

    private void applytoproblem(List<Double> prob){
        List<Double> temp = new ArrayList<>();
        for (List<Integer> seq : this.listofsequence.keySet()){
            temp = rearrange(prob, seq);
            this.applyproblemoperator(temp, this.listofsequence.get(seq));
        }
    }

    private List<Double> rearrange(List<Double> l, List<Integer> idxseq){
        List<Double> shuffled = new ArrayList<>();
        for (int i = 0; (i < l.size()); i++){
            shuffled.add(l.get(idxseq.get(i)));
        }
        return shuffled;
    }

    /* problem apply operation */
    private void applyproblemoperator(List<Double> paramprob, List<String> paramop){
        List<Double> pat = new ArrayList<>(paramprob);
        List<String> op = new ArrayList<>(paramop);

        double result = pat.get(0);
        for (int n = 1, p = 0; (n < pat.size()) && (p < op.size()); n++, p++){
            switch (op.get(p)) {
                case "+":
                    result = result + pat.get(n).doubleValue();
                    break;
                case "-":
                    result = result - pat.get(n).doubleValue();
                    break;
                case "*":
                    result = result * pat.get(n).doubleValue();
                    break;
                case "/":
                    result = result / pat.get(n).doubleValue();
                    break;
                default:
                    break;
            }
        }

        // Insert answer
        ProbableAnswer answerlist = new ProbableAnswer();
        answerlist.problemseq = paramprob;
        answerlist.operator = paramop;
        answerlist.value = result;
        this.finalresult.add(answerlist);
    }

    /* permute an index sequence of pattern */
    private Map<List<Integer>, List<String>> permutesequence(){
        List<Integer> onelos = new ArrayList<>();
        Map<List<Integer>, List<String>> los = new HashMap<>();

        for (List<T> l : this.eligiblepattern.keySet()){
            onelos = new ArrayList<>();
            for (T t : l){
                onelos.add(this.pattern.indexOf(t));
            }
            los.put(onelos, this.eligiblepattern.get(l));
        }
        return los;
    }

    /* pattern apply operation */
    private void applypatternoperator(List<T> parampat, List<String> paramop){
        List<T> pat = new ArrayList<>(parampat);
        List<String> op = new ArrayList<>(paramop);

        double result = pat.get(0).doubleValue();
        for (int n = 1, p = 0; (n < pat.size()) && (p < op.size()); n++, p++){
            switch (op.get(p)) {
                case "+":
                    result = result + pat.get(n).doubleValue();
                    break;
                case "-":
                    result = result - pat.get(n).doubleValue();
                    break;
                case "*":
                    result = result * pat.get(n).doubleValue();
                    break;
                case "/":
                    result = result / pat.get(n).doubleValue();
                    break;
                default:
                    break;
            }
        }
        if (result == this.ptrtarget.doubleValue()){
            this.eligiblepattern.put(pat, op);
        }
    }

    /* permutation of pattern */
    private void permute(List<T> ptrn, int l, int r){
        if (l == r){
            List<T> porary = new ArrayList<>(ptrn);
            this.patpermute.add(porary);
        }
        else{ 
            for (int i = l; i <= r; i++) {
                ptrn = swap(ptrn, l, i);
                permute(ptrn, l + 1, r); 
                ptrn = swap(ptrn, l, i);
            }
        } 
    } 
    private List<T> swap(List<T> ptrn, int i, int j) { 
        T temp = ptrn.get(i) ;
        ptrn.set(i, ptrn.get(j));
        ptrn.set(j, temp);
        return ptrn; 
    }

    /* permutation of operator */
    private void permuteoperator(){
        List<String> pivot = new ArrayList<>();
        for (int i = 0; i < this.pattern.size() - 2; i++){
            pivot.add("+");
        }
        this.oprecursive(pivot);
    }

    private void oprecursive(List<String> list){
        List<String> temp = new ArrayList<>();
        for (String op : this.operatorlist){
            temp = new ArrayList<>(list);
            temp.set(temp.size() - 1, op);
            // System.out.println(temp.toString());
            this.oprtpermute.add(temp);
        }
        if (isAllSlash(temp)){
            return;
        }
        temp = leftcheck(temp, temp.size() - 1);
        this.oprecursive(temp);
    }
    private boolean isAllSlash(List<String> list){
        boolean valid = true;
        for (String op : list){
            if (!op.equals("/")){ valid = false; }
        }
        return valid;
    }
    private List<String> leftcheck(List<String> l, int idx){
        // System.out.println(idx);
        if (l.get(idx).equals("/") && idx != 0){
            leftcheck(l, idx - 1);
            l.set(idx, this.operatorlist.get(0)); // kolom ini jadi plus lagi
        }
        else if (l.get(idx).equals("/") && idx == 0){}
        else{
            switch (l.get(idx)) {
                case "+":
                    l.set(idx, this.operatorlist.get(1));
                    break;
                case "-":
                    l.set(idx, this.operatorlist.get(2));
                    break;
                case "*":
                    l.set(idx, this.operatorlist.get(3));
                    break;
            }
        }
        return l;
    }
}
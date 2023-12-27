package rendering;

public class Bajs {
    public enum Move{
        R(false, Side.R),L(false, Side.L),U(false, Side.U), D(false, Side.D),B(false, Side.B),F(false, Side.F),
        R_(true, Side.R),L_(true, Side.L),U_(true, Side.U),D_(true, Side.D),B_(true, Side.B) ,F_(true, Side.F);
        
        public final boolean prime;
        public final Side side;
        Move(boolean prime, Side side){
            this.prime = prime;
            this.side = side;
        }
    }
    public enum Side{
        R, L, U, D, B, F
    }
}

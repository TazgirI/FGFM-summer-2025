package net.tazgirl.fgfmsummer.dirty;

public class TickTimer
{

    public static double tickTimer(double timer)
    {
        // Work in integer hundredths to avoid floating point drift
        long hundredths = Math.round(timer * 100.0);

        // decrement by one hundredth (i.e. 0.01)
        hundredths -= 1L;

        // clamp negative to zero (change this if you want wrap-around behavior)
        if (hundredths < 0L) {
            return 0.0;
        }

        int frac = (int) (hundredths % 100L); // 0..99

        // if fractional part is >= 60 (i.e. 0.60..0.99) snap it to .59
        if (frac >= 60) {
            hundredths = (hundredths / 100L) * 100L + 59L;
        }

        return hundredths / 100.0;
    }

}

package com.prediction;

public class KalmanFilter {

    private double f;
    private double q;
    private double h;
    private double r;

    private double state;
    private double covariance;

    public void setState(double state, double covariance) {
        this.state = state;
        this.covariance = covariance;
    }

    public KalmanFilter(double f, double q, double h, double r) {

        this.f = f;
        this.q = q;
        this.h = h;
        this.r = r;
    }

    public double getState() {
        return state;
    }

    public void correct(double data){
        //time update - prediction
        double x0 = f * state;
        double p0 = f * covariance * f + q;

        //measurement update - correction
        double K = h* p0 /(h*p0*h + r);
        state = x0 + K*(data - h* x0);
        covariance = (1 - K*h)* p0;
    }
}

package org.m3studio.signalanalyzer.DSP;

import java.util.regex.Pattern;

/**
 * Created by madmax on 06.03.15.
 */
public class Complex {
    private double re;
    private double im;

    public Complex() {
        re = 0.0;
        im = 0.0;
    }

    public Complex(double real, double imag) {
        re = real;
        im = imag;
    }

    public Complex(Complex copyThis) {
        re = copyThis.re;
        im = copyThis.im;
    }

    public String toString() {
        if (im == 0) return re + "";
        if (re == 0) return im + "i";
        if (im < 0) return re + " - " + (-im) + "i";
        return re + " + " + im + "i";
    }

    public double abs() {
        return Math.hypot(re, im);
    }

    public double arg() {
        return Math.atan2(im, re);
    }

    public Complex plus(Complex b) {
        re += b.re;
        im += b.im;
        return this;
    }

    public Complex minus(Complex b) {
        re -= b.re;
        im -= b.im;
        return this;
    }

    public Complex times(Complex b) {
        double real = re * b.re - im * b.im;
        double imag = re * b.im + im * b.re;

        re = real;
        im = imag;

        return this;
    }

    public Complex times(double alpha) {
        re *= alpha;
        im *= alpha;
        return this;
    }

    public Complex conjugate() {
        im = -im;
        return this;
    }

    public Complex reciprocal() {
        double scale = re * re + im * im;
        re /= scale;
        im /= (-scale);
        return this;
    }

    // return the real or imaginary part
    public double re() {
        return re;
    }

    public double im() {
        return im;
    }

    public Complex set(double re, double im) {
        this.re = re;
        this.im = im;

        return this;
    }

    public Complex set(Complex b) {
        this.re = b.re;
        this.im = b.im;

        return this;
    }

    public Complex divides(Complex b) {
        return times(Complex.reciprocal(b));
    }

    /*// return a new Complex object whose value is the complex exponential of this
    public Complex exp() {
        return new Complex(Math.exp(re) * Math.cos(im), Math.exp(re) * Math.sin(im));
    }

    // return a new Complex object whose value is the complex sine of this
    public Complex sin() {
        return new Complex(Math.sin(re) * Math.cosh(im), Math.cos(re) * Math.sinh(im));
    }

    // return a new Complex object whose value is the complex cosine of this
    public Complex cos() {
        return new Complex(Math.cos(re) * Math.cosh(im), -Math.sin(re) * Math.sinh(im));
    }

    // return a new Complex object whose value is the complex tangent of this
    public Complex tan() {
        return sin().divides(cos());
    }*/

    public static Complex plus(Complex a, Complex b) {
        return new Complex(a).plus(b);
    }

    public static Complex minus(Complex a, Complex b) {
        return new Complex(a).minus(b);
    }

    public static Complex times(Complex a, Complex b) {
        return new Complex(a).times(b);
    }

    public static Complex times(Complex a, double alpha) {
        return new Complex(a).times(alpha);
    }

    public static Complex conjugate(Complex a) {
        return new Complex(a).conjugate();
    }

    public static Complex reciprocal(Complex a) {
        return new Complex(a).reciprocal();
    }

    public static Complex divides(Complex a, Complex b) {
        return new Complex(a).divides(b);
    }

}

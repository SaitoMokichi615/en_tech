set terminal png
set output "Radioactive_decay.png"
set title "Radioactive decay"
set xlabel "t"
set ylabel "N"
lambda = 0.08664;
N = 100
f(x) = N*exp(-1*lambda*x)
plot "RungeKutta.txt" title "Predict(Runge-Kutta)" lc rgb "red", f(x) title "True(N_0exp(-lambda*t))" lw 2 lc rgb "green"

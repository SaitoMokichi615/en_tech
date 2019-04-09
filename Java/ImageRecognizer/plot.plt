#unset key
set terminal png

set key bottom
set output "accuracy.png"
set title "Accuracy Transition (AdaGrad)"
#set yrange[0:1.0]
#set ytic 0.1
set xlabel "epochs"
set ylabel "accuracy"
plot "accuracyTransition-100hiddenN-3Layers-He-AdaGrad.txt" with line lc rgb "blue" lw 2 title "100 Neurons",\
 "accuracyTransition-100batchiSize-3Layers-He-AdaGrad.txt" with line lc rgb "red" lw 2  title"50 Neurons",\
 #"accuracyTransition-100batchiSize-2Layers-He-SGD.txt" with line lc rgb "red" lw 2 title"2 Layers net"

set logscale y
set key top
#set xrange[0:1000]
#set yrange[0:2.5]
set output "loss.png"
set title "Loss Transition (SGD)"
set xlabel "iteration"
set ylabel "loss"
plot "lossTransition-100hiddenN-3Layers-He-AdaGrad.txt" with line lc rgb "blue" title"100 Neurons",\
"lossTransition-100batchiSize-3Layers-He-AdaGrad.txt" with line lc rgb "red" title"50 Neurons",\
#"lossTransition-100batchiSize-2Layers-He-SGD.txt" with line lc rgb "red" title"2 Layers net"
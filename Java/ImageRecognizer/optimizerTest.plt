set terminal png
set nokey
#set key bottom
set output "grad.png"
set title "Accuracy Transition (AdaGrad)"

set view 0,0
set nosurface
set contour base
set cntrparam levels incremental 0, 500, 10000 
set xrange [-100:100]
set yrange [-100:100]
splot x*x/20 + y*y
plot x

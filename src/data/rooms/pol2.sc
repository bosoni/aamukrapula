# pol asema
int info=0

# entered funktiota kutsutaan kun huoneeseen tullaan
func entered()
endfunc

func suihku()
	if action["suihku"]==0
		MessageBox("K�vit suihkussa. Ei pit�isi en�� haista kusi ja yrj�!")
		action["suihku"]=1
	endif

endfunc

func eisinne()
	if info==0
		MessageBox("Et halua menn� tyhj��n huoneeseen, haluat baariin!")
		info=1
	endif

endfunc

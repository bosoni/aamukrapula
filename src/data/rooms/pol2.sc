# pol asema
int info=0

# entered funktiota kutsutaan kun huoneeseen tullaan
func entered()
endfunc

func suihku()
	if action["suihku"]==0
		MessageBox("Kävit suihkussa. Ei pitäisi enää haista kusi ja yrjö!")
		action["suihku"]=1
	endif

endfunc

func eisinne()
	if info==0
		MessageBox("Et halua mennä tyhjään huoneeseen, haluat baariin!")
		info=1
	endif

endfunc

# vessa

# entered funktiota kutsutaan kun huoneeseen tullaan
func entered()
	MessageBox("Hyi 100N! Melkein lensi yrj� samantien kun astuit vessaan.")

endfunc

func oksenna()
	if action["yrjo"]==0
		MessageBox("Oksensit ja olosi v�h�n helpotti. Nyt maistuisi olut.")
		action["yrjo"]=1
	endif

endfunc

# vessa

# entered funktiota kutsutaan kun huoneeseen tullaan
func entered()
	MessageBox("Hyi 100N! Melkein lensi yrjö samantien kun astuit vessaan.")

endfunc

func oksenna()
	if action["yrjo"]==0
		MessageBox("Oksensit ja olosi vähän helpotti. Nyt maistuisi olut.")
		action["yrjo"]=1
	endif

endfunc

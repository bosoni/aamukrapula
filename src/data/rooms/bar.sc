# baarin edusta

# entered funktiota kutsutaan kun huoneeseen tullaan
func entered()
	if action["liiskainfo"]==0
		MessageBox("Heh, joku ei ole tainut varoa rattijuoppoja..")
		action["liiskainfo"]=1
	endif

endfunc

func otarahat()
	if action["rahat"]==0
		MessageBox("Tutkisit ihmismaton ja l�ysit hieman rahaa.\nJees, kohta p��see bisselle!")
		action["rahat"]=1
	endif

endfunc

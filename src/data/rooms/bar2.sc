# baarin sis‰ll‰

# entered funktiota kutsutaan kun huoneeseen tullaan
func entered()
	if action["suihku"]==0
		MessageBox("\"Hyi helvetti, mik‰ oksennuksen lˆyhk‰ tuosta tyypist‰ l‰htee..\"")
	endif

endfunc

func baarimikko()
	LoadAndDraw("naama.PNG", 200, 200)
	Delay(1000)

	if action["yrjo"]==0
		MessageBox("Sinua oksettaa niin kovin ettet viel‰ saa mit‰‰n juotavaa alas.")
	elseif action["suihku"]==0
		LoadAndDraw("baari_naama.JPG", 150, 200)
		Delay(1000)
		MessageBox("\"Yhh..haiset niin pahalle etten myy sulle mit‰‰n, \nhaluan sut vain ulos baaristani!\"")
	elseif action["rahat"]==0
		LoadAndDraw("baari_naama.JPG", 150, 200)
		Delay(1000)
		MessageBox("\"En harrasta hyv‰ntekev‰isyytt‰! Rahalla saat juotavaa!\"")
	else
		LoadAndDraw("baari_naama.JPG", 150, 200)
		MessageBox("\"Huomenta. Nyt maistuisi pitk‰ olut!\" sanoit baarimikolle.\n\"Tulee.\" vastasi baarimikko.")
		MessageBox("Ja pian alkaa el‰m‰ j‰lleen hymyilem‰‰n.\nOnneksi olkoon, pelasit pelin l‰pi.\n\nby mjt, 2007 miksuu_79@hotmail.com")

		EndGame()
	endif

	RemoveImage()

endfunc

func muisto()
	LoadAndDraw("dream.PNG", 100, 100)
	Delay(1000)
	MessageBox("Mukavia muistoja tulee mieleesi..")
	RemoveImage()

endif

endfunc

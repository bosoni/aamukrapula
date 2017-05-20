# kujalla

# entered funktiota kutsutaan kun huoneeseen tullaan
func entered()
	if action["alkutxt"]==0
    	LoadAndDraw("yrjo.jpg", 120, 120)
		Delay(1000)
		MessageBox("SPEditorilla tehty yksinkertainen seikkailupeli, Aamukrapula.\nHiiren oikea n‰pp‰in vaihtaa toiminnin (k‰vele, katso, ota/k‰yt‰),\nvasen n‰pp‰in toteuttaa (hiirikursorin sinisen nuolen kohdassa).\n Peliss‰ ei ole reitinhakua joten ukko ei kierr‰ esteit‰ automaattisesti.")
		RemoveImage();

		MessageBox("Her‰sit kujalta. Olosi on hirve‰, oksettaa. \nEilisest‰ et muista mit‰‰n mutta kaikki rahasi ainakin olet saanut menem‰‰n.\nHaistat kusen ja oksennuksen, toivot ettei haju l‰hde sinusta.")

		action["alkutxt"]=1

	endif

endfunc

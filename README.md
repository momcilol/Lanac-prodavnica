Integracija Sistema - Zadatak
=============================

Implementirati distribuiranu aplikaciju za proveru dostupnosti proizvoda u
prodavnicama jedne kompanije koja se bavi prodajom elektronike pomocu Java
RMI po uputstvima u nastavku teksta.

Server prilikom pokretanja registruje udaljeni objekat za na lokalnom racunaru
na standardnom portu pod imenom "itshop". Ovaj objekat implementira dati
interfejs:  

```
interface StoreServer {  
	public List<String> checkAvailable(String product);  
	public void addStore(Store store);  
	public void removeStore(Store store);  
}  
```

```
interface Store {  
	public int checkAvailable(String product);  
}  
```

Klijent prilikom pokretanja dobavlja serverski objekat. Host i port na kojem se
nalazi serverski objekat se zadaju preko drugog argumenta komandne linije u
obliku "host:port". U prvom argumentu komandne linije se nalazi identifikator
prodavnice (npr. 001-ns-futoska, 002-bg-usce, 003-bg-slavija, 004-kg-centar,
005-ns-big...).

Pre povezivanja sa serverom, klijent ucitava spisak proizvoda koji se nalaze u
prodavnici sa zadatim identifikatorom iz xml fajla cije ime odgovara
identifikatoru (npr. 001-ns-futoska.xml, 002-bg-usce.xml...)

Ovi XML dokumenti zadovoljavaju sledeci DTD:  
```
<!--ELEMENT magacin (proizvod*) -->  
<!--ELEMENT proizvod (#PCDATA) -->  
<!--ATTLIST proizvod kolicina CDATA #REQUIRED-->  
```
Posle uspesnog ucitavanja spiska proizvoda i povezivanja sa serverom, klijent
se registruje i u petlji nudi mogucnost korisniku da izvrsava komande opisane
u nastavku teksta.

Klijent prekida svoj rad kada korisnik unese prazanu komandu. Pre prekida rada,
klijent snima svoj (eventualno izmenjeni) spisak proizvoda nazad u fajl i
deregistruje se sa servera.

Klijent prepoznaje sledece komande:

- ? proizvod - Ispisuje kolicinu datog proizvoda raspolozivog u prodavnici, ako
               prodavnica ima bar jedan takav proizvod. Ako prodavnica ne
               raspolaze datim proizvodom, salje upit serveru i ispisuje
               odgovor sa servera, odnosno, u kojim prodavnicama se taj
               proizvod moze pronaci.

- ! proizvod - Prodaje jedan komad naznacenog proizvoda. Ako prodavnica nema
               dati proizvod, ispisuje poruku o gresci.

- prazan unos - Kraj rada programa

Jedini argument komande predstavlja sifru zeljenog proizvoda (npr. logi-mis-m320,
genius-zvucnici-z2255, logi-tastatura-k880...).

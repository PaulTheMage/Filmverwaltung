In SQl haben wir die Tabellen:
Filme:
CREATE TABLE filme (
id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
imdb_id VARCHAR(20) UNIQUE,
titel VARCHAR(255) NOT NULL,
erscheinungsjahr INTEGER,
genre VARCHAR(100),
regisseur VARCHAR(255),
laufzeit_minuten INTEGER
);
Bewertungen:
CREATE TABLE bewertungen (
id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
film_id BIGINT NOT NULL,
bewertung NUMERIC(2,1) NOT NULL,
kommentar TEXT,
erstellt_am TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_bewertung_film
        FOREIGN KEY (film_id)
        REFERENCES filme(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_bewertung
        CHECK (bewertung >= 0 AND bewertung <= 10)
);
Schauspieler:
CREATE TABLE schauspieler (
id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
vorname VARCHAR(100) NOT NULL,
nachname VARCHAR(100) NOT NULL,
geburtsdatum DATE
);

In Java haben wir die Main.java mit dem Menü.
Wir haben eine Klasse mit dem API Call.
Wir haben eine Klasse für das Objekt Film.
Wir haben eine Klasse für die DatabaseConnection.
Dann haben wir eine Klasse für die SQL Operationen:
Methoden:  
    Film hinzufügen (Mit dem API Call): addMovie
    Filme ansehen:                      displayMovies
    Filmbewertung eintragen:            addRating
    Film löschen:                       deleteMovie
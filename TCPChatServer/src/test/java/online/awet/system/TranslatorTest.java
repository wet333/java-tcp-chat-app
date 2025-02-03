package online.awet.system;

import online.awet.system.core.parser.Translator;
import online.awet.system.core.parser.TranslatorException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TranslatorTest {

    Translator translator = Translator.getInstance();

    @Test
    void translate() {
        // Must be valid
        assertEquals("REGISTER:user=wet54321;pass=password123;", translator.translate("/register -user wet54321 -pass password123"));
        assertEquals("LOGIN:user=wet54321;pass=password123;", translator.translate("/login -user wet54321 -pass password123"));
        assertEquals("ADD-FRIEND:user=wet54321;", translator.translate("/addFriend -user wet54321"));
        assertEquals("DELETE-FRIEND:user=wet54321;", translator.translate("/deleteFriend -user wet54321 "));
        assertEquals("SEND-FILE:user=wet54321;fileId=549876541321567;fileName=photo.png;", translator.translate("/sendFile -user wet54321 -fileId 549876541321567 -fileName photo.png"));

        // Empty values must fail
        assertThrows(TranslatorException.class, () -> {translator.translate("");} , "Empty string");
        assertThrows(TranslatorException.class, () -> {translator.translate("       ");}, "Only spaces string");
        assertThrows(TranslatorException.class, () -> {translator.translate("/");}, "Only / string");
        assertThrows(TranslatorException.class, () -> {translator.translate("     /");}, "Spaces + /");
        assertThrows(TranslatorException.class, () -> {translator.translate("/      ");}, "/ + spaces");
        assertThrows(TranslatorException.class, () -> {translator.translate("  /  ");}, "Spaces + / + spaces");
    }

    @Test
    void isServerAction() {
        assertTrue(translator.isServerAction("/addFriend -user wet54321"));

        assertFalse(translator.isServerAction(""));
        assertFalse(translator.isServerAction("    "));
        assertFalse(translator.isServerAction(" "));
        assertFalse(translator.isServerAction("   asdasd  asd  "));

    }

    @Test
    void camelToHyphenatedCaps() {
        assertEquals("THIS-IS-A-CAMEL-CASE-STRING", translator.camelToHyphenatedCaps("thisIsACamelCaseString"));
        assertEquals("WORD-PAIR", translator.camelToHyphenatedCaps("wordPair"));
        assertEquals("SINGLE", translator.camelToHyphenatedCaps("single"));
        assertEquals("A-L-R-E-A-D-Y-U-P-P-E-R-C-A-S-E", translator.camelToHyphenatedCaps("ALREADYUPPERCASE"));
        assertEquals("A", translator.camelToHyphenatedCaps("a"));
        assertEquals("A-B-C", translator.camelToHyphenatedCaps("aBC"));

        // Numbers not allowed
        assertThrows(TranslatorException.class, () -> { translator.camelToHyphenatedCaps("withNumbers123AndSymbols");}, "No numbers allowed.");
        assertThrows(TranslatorException.class, () -> { translator.camelToHyphenatedCaps("");}, "No empty strings.");
        assertThrows(TranslatorException.class, () -> { translator.camelToHyphenatedCaps("    ");}, "No only spaces allowed.");
        assertThrows(TranslatorException.class, () -> { translator.camelToHyphenatedCaps(null);}, "No null allowed");

    }
}
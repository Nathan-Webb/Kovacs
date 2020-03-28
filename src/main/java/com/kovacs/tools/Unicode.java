/*
 *    Copyright 2020 Nathan Webb
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.kovacs.tools;

import com.ibm.icu.text.Normalizer2;
import com.ibm.icu.text.SpoofChecker;
import com.vdurmont.emoji.EmojiParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class Unicode {

    private static SpoofChecker spoofChecker;
    private static Normalizer2 normalizer;
final static Logger logger = LoggerFactory.getLogger(Unicode.class);

    public static Normalizer2 getNormalizer() {
        return normalizer;
    }

    public static void setNormalizer(Normalizer2 normalizer) {
        Unicode.normalizer = normalizer;
    }


    public static SpoofChecker getSpoofChecker() {
        return spoofChecker;
    }

    public static void setSpoofChecker(SpoofChecker checker){
        spoofChecker = checker;
    }

    //this should only be used for checking against a word filter, no public facing stuff
    public static String getSkeletonFilter(String s){
        return spoofChecker.getSkeleton(s);
    }

    //public facing
    public static String normalize(CharSequence charSequence){
         return normalizer.normalize(charSequence);
    }

    public static boolean isHoistChar(Character c){
        return (32 <= (int) c && (int) c <= 64) //ascii digits and punctuation
                || (91 <= (int) c && (int) c <= 96)  //ascii punctuation
                || (123 <= (int) c && (int) c <= 126); //ascii punctuation
            //33-64 91-96 123-126
    }

    public static boolean isUnmentionableChar(Character c){
        return (160 <= (int) c && (int) c <= 191) //latin punctuation
                || (384 <= (int) c && (int) c <= 451) //not-european & historic latin + african clicks
                || (477 <= (int) c && (int) c <= 505) //phonetic and historic letters sans WGL4
                || (540 <= (int) c && (int) c <= 553) //misc letters
                || (688 <= (int) c && (int) c <= 767) //spacing modifier letters
                || (880 <= (int) c) //greek and coptic, and beyond
                ;
    }

    public static String cleanEverything(String string){
        return EmojiParser.removeAllEmojis(normalizeAndRemoveUselessChars(string));
    }

    public static String normalizeAndRemoveSpoofs(String string){
        String normalized = Unicode.normalize(string);
        char[] normArr = normalized.toCharArray();
        StringBuilder newString = new StringBuilder();
        for(int i = 0; i < normalized.length(); i++){
            char c = normArr[i];
            if(Unicode.isUnmentionableChar(c)){
                newString.append(Unicode.getSpoofChecker().getSkeleton(String.valueOf(c)));
            } else {
                newString.append(c);
            }
        }
        return newString.toString();
    }

    public static String normalizeAndRemoveUselessChars(String string){
        String normalized = Unicode.normalize(string);
        return removeUselessChars(normalized);
    }

    public static String removeUselessChars(String string){
        char[] normArr = string.toCharArray();
        StringBuilder newString = new StringBuilder();
        for(int i = 0; i < string.length(); i++){
            char c = normArr[i];
            if(!Unicode.isUnmentionableChar(c)){ //isn't useless, append
                newString.append(c);
            }
        }
        return newString.toString();
    }

    public static boolean isHoisting(String s){
        char[] array = s.toCharArray();
        return Unicode.isHoistChar(array[0]);
    }

    public static String dehoist(String string){
        String finalTrimmed;
        char[] nameArray = string.toCharArray();
        int indexLeft;
        for(indexLeft = 0; indexLeft < string.length(); indexLeft++) {
            char c = nameArray[indexLeft];
            if (!Unicode.isHoistChar(c)) { //not hoisting
                break;
            }
        }

        String trimmedLeft = string.substring(indexLeft).trim();
        boolean botherWithRight = true;
        if(trimmedLeft.equals("")){ //very bad hoister
            botherWithRight = false; //dont even bother with that reverse loop
            finalTrimmed = Config.getString("fallbackName");
        } else {
            finalTrimmed = trimmedLeft;
        }

        if(botherWithRight){ //reverse loop so that we can deal with !hoisting! to make the member list look ok
            char[] trimmedLeftArray = trimmedLeft.toCharArray();
            int indexRight;
            for(indexRight = trimmedLeftArray.length - 1; indexRight > 0; indexRight--) {
                char c = trimmedLeftArray[indexRight];
                if (!Unicode.isHoistChar(c)) { //not hoisting
                    break;
                }
                //33-64 91-96 123-126
            }
            finalTrimmed = trimmedLeft.substring(0, indexRight + 1);
        }
        return finalTrimmed;
    }


}

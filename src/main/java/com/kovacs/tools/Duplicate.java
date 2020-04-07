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

public class Duplicate {
    private int amountSent;
    private String userID;
    private String message;
    private long epochOfLastMessage;

    public Duplicate(String userID, String message, long epochOfMessage){
        this.userID = userID;
        this.message = message;
        this.epochOfLastMessage = epochOfMessage;
        amountSent = 0;
    }

    public int getAndAddOne(){
        amountSent++;
        return amountSent;
    }

    public int getAmountSent() {
        return amountSent;
    }

    public String getUserID() {
        return userID;
    }

    public String getMessage() {
        return message;
    }

    public void resetWithNewMessage(String s, long epochOfMessage){
        this.amountSent = 0;
        this.message = s;
        this.epochOfLastMessage = epochOfMessage;
    }

    public long getEpochOfLastMessage() {
        return epochOfLastMessage;
    }
}

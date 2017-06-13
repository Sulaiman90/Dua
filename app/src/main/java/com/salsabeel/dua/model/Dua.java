package com.salsabeel.dua.model;



public class Dua {

    private int duaId;
    private String arabicDua;
    private String englishTranslation;
    private String tamilTranslation;
    private String englishReference;
    private String tamilReference;
    private int groupId;
    private boolean fav;
    private int duaSerialNo;

    public Dua (int duaId, int duaSerialNo ,String arabicDua, String englishTranslation, String tamilTranslation,
                String englishReference, String tamilReference ,boolean fav){
        this.duaId = duaId;
        this.duaSerialNo = duaSerialNo;
        this.arabicDua = arabicDua;
        this.englishTranslation = englishTranslation;
        this.tamilTranslation = tamilTranslation;
        this.englishReference = englishReference;
        this.tamilReference = tamilReference;
        this.fav = fav;
    }

    public int getDuaSerialNo() {
        return duaSerialNo;
    }

    public void setDuaSerialNo(int duaSerialNo) {
        this.duaSerialNo = duaSerialNo;
    }


    public int getDuaId() {
        return duaId;
    }

    public void setDuaId(int duaId) {
        this.duaId = duaId;
    }

    public boolean isFav() {
        return fav;
    }

    public void setFav(boolean fav) {
        this.fav = fav;
    }

    public String getArabicDua() {
        return arabicDua;
    }

    public void setArabicDua(String arabicDua) {
        this.arabicDua = arabicDua;
    }

    public String getEnglishTranslation() {
        return englishTranslation;
    }

    public void setEnglishTranslation(String englishTranslation) {
        this.englishTranslation = englishTranslation;
    }

    public String getTamilTranslation() {
        return tamilTranslation;
    }

    public void setTamilTranslation(String tamilTranslation) {
        this.tamilTranslation = tamilTranslation;
    }

    public String getEnglishReference() {
        return englishReference;
    }

    public void setEnglishReference(String englishReference) {
        this.englishReference = englishReference;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getTamilReference() {
        return tamilReference;
    }

    public void setTamilReference(String tamilReference) {
        this.tamilReference = tamilReference;
    }

}

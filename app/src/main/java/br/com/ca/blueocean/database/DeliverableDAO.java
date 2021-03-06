package br.com.ca.blueocean.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.com.ca.blueocean.vo.DeliverableVo;

/**
 * DeliverableDAO
 *
 * DAO for DELIVERABLE yabee local table
 *
 * @author Rodrigo Carvalho
 */
public class DeliverableDAO {
    private DatabaseOpenHelper databaseOpenHelper;
    private SQLiteDatabase db;

    //Constructor
    public DeliverableDAO(Context context){
        this.databaseOpenHelper = DatabaseOpenHelper.getInstance(context);
    }

    //
    // HELPER METHODS
    //

    //
    //insert deliverable
    //
    public void insertDeliverableVo(DeliverableVo deliverableVo){
        SQLiteDatabase db = this.databaseOpenHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(DatabaseOpenHelper.KEY_DELIVERABLE_iddeliverable, deliverableVo.getIddeliverable());
        values.put(DatabaseOpenHelper.KEY_DELIVERBALE_INITIATIVE_idinititative, deliverableVo.getIdInitiative());
        values.put(DatabaseOpenHelper.KEY_DELIVERABLE_title, deliverableVo.getTitle());
        values.put(DatabaseOpenHelper.KEY_DELIVERABLE_description, deliverableVo.getDescription());
        values.put(DatabaseOpenHelper.KEY_DELIVERABLE_comment, deliverableVo.getComments());
        values.put(DatabaseOpenHelper.KEY_DELIVERABLE_status, deliverableVo.getStatus());
        values.put(DatabaseOpenHelper.KEY_DELIVERABLE_duedate, deliverableVo.getDuedate());
        values.put(DatabaseOpenHelper.KEY_DELIVERABLE_idresponsible, deliverableVo.getIdresponsibleuser());
        values.put(DatabaseOpenHelper.KEY_DELIVERABLE_rating, deliverableVo.getRating());
        values.put(DatabaseOpenHelper.KEY_DELIVERABLE_ispriority, deliverableVo.getIsPriority());
        values.put(DatabaseOpenHelper.KEY_DELIVERABLE_prioritycomment, deliverableVo.getPriorityComment());
        values.put(DatabaseOpenHelper.KEY_DELIVERABLE_prioritizedby, deliverableVo.getPrioritizedBy());
        values.put(DatabaseOpenHelper.KEY_DELIVERABLE_deliverablevalue, deliverableVo.getDeliverableValue());
        values.put(DatabaseOpenHelper.KEY_DELIVERABLE_code, deliverableVo.getCode());
        values.put(DatabaseOpenHelper.KEY_DELIVERABLE_currentusername, deliverableVo.getCurrentusername());

        // Inserting Row
        db.insert(DatabaseOpenHelper.DATABASE_TABLE_DELIVERABLE, null, values);
        db.close(); // Closing database connection
    }

    //
    //return selected list of deliverables
    //
    public List<DeliverableVo> selectDeliverables(){
        List<DeliverableVo> deliverableVoList =  new ArrayList<DeliverableVo>();
        SQLiteDatabase db = this.databaseOpenHelper.getReadableDatabase();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + DatabaseOpenHelper.DATABASE_TABLE_DELIVERABLE;

        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                DeliverableVo deliverableVo = new DeliverableVo(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getString(9),
                        cursor.getString(10),
                        cursor.getString(11),
                        cursor.getString(12),
                        cursor.getString(13),
                        cursor.getString(14)
                        );

                // Adding contact to list
                deliverableVoList.add(deliverableVo);

            } while (cursor.moveToNext());
        }

        // return Initiative list
            return deliverableVoList;
    }

    //
    //return selected list of deliverables by initiative id
    //
    public List<DeliverableVo> selectDeliverablesByInitiativeId(String initiativeId){
        List<DeliverableVo> deliverableVoList =  new ArrayList<DeliverableVo>();
        SQLiteDatabase db = this.databaseOpenHelper.getReadableDatabase();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + DatabaseOpenHelper.DATABASE_TABLE_DELIVERABLE + " WHERE  " + DatabaseOpenHelper.KEY_DELIVERABLE_status + " = 'OPEN' AND " + DatabaseOpenHelper.KEY_DELIVERBALE_INITIATIVE_idinititative + " = '" + initiativeId + "'";
        Cursor cursor = db.rawQuery(selectQuery, null); // db.rawQuery(selectQuery, query_params)

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                DeliverableVo deliverableVo = new DeliverableVo(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getString(9),
                        cursor.getString(10),
                        cursor.getString(11),
                        cursor.getString(12),
                        cursor.getString(13),
                        cursor.getString(14)
                );

                // Adding deliverable to list
                deliverableVoList.add(deliverableVo);
            } while (cursor.moveToNext());
        }

        // return Initiative list
        return deliverableVoList;
    }

    //
    //return selected list of deliverables by initiative id
    //
    public List<DeliverableVo> selectFinishedDeliverablesByInitiativeId(String initiativeId){
        List<DeliverableVo> deliverableVoList =  new ArrayList<DeliverableVo>();
        SQLiteDatabase db = this.databaseOpenHelper.getReadableDatabase();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + DatabaseOpenHelper.DATABASE_TABLE_DELIVERABLE + " WHERE  " + DatabaseOpenHelper.KEY_DELIVERABLE_status + " = 'FINISHED' AND " + DatabaseOpenHelper.KEY_DELIVERBALE_INITIATIVE_idinititative + " = '" + initiativeId + "'";
        Cursor cursor = db.rawQuery(selectQuery, null); // db.rawQuery(selectQuery, query_params)

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                DeliverableVo deliverableVo = new DeliverableVo(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getString(9),
                        cursor.getString(10),
                        cursor.getString(11),
                        cursor.getString(12),
                        cursor.getString(13),
                        cursor.getString(14)
                );

                // Adding deliverable to list
                deliverableVoList.add(deliverableVo);
            } while (cursor.moveToNext());
        }

        // return Initiative list
        return deliverableVoList;
    }

    /**
     * getDeliverableById
     *
     * @param deliverableId
     * @return
     */
    public DeliverableVo getDeliverableById(String deliverableId){
        DeliverableVo deliverableVo = null;
        SQLiteDatabase db = this.databaseOpenHelper.getReadableDatabase();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + DatabaseOpenHelper.DATABASE_TABLE_DELIVERABLE + " WHERE  " + DatabaseOpenHelper.KEY_DELIVERABLE_status + " = 'OPEN' AND " + DatabaseOpenHelper.KEY_DELIVERABLE_iddeliverable + " = '" + deliverableId + "'";

        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                deliverableVo = new DeliverableVo(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getString(9),
                        cursor.getString(10),
                        cursor.getString(11),
                        cursor.getString(12),
                        cursor.getString(13),
                        cursor.getString(14)
                );

            } while (cursor.moveToNext());
        }

        // return Initiative list
        return deliverableVo;
    }

    /**
     * getPrioritizedDeliverables
     *
     * @param filter
     * @return
     */
    public List<DeliverableVo> getPrioritizedDeliverables(String filter, String userId){

        List<DeliverableVo> deliverableVoList =  new ArrayList<DeliverableVo>();
        DeliverableVo deliverableVo = null;
        SQLiteDatabase db = this.databaseOpenHelper.getReadableDatabase();

        String selectQuery = null;

        // Select All Query
        if (filter.equals("USER")) {
            selectQuery = "SELECT  * FROM " + DatabaseOpenHelper.DATABASE_TABLE_DELIVERABLE + " WHERE " + DatabaseOpenHelper.KEY_DELIVERABLE_status + " = 'OPEN' AND " + DatabaseOpenHelper.KEY_DELIVERABLE_ispriority + " = 'YES'" + " AND " + DatabaseOpenHelper.KEY_DELIVERABLE_prioritizedby + " = '" + userId + "'";

        } else if (filter.equals("ALL")) {
            selectQuery = "SELECT  * FROM " + DatabaseOpenHelper.DATABASE_TABLE_DELIVERABLE + " WHERE " + DatabaseOpenHelper.KEY_DELIVERABLE_status + " = 'OPEN' AND " + DatabaseOpenHelper.KEY_DELIVERABLE_ispriority + " = 'YES'";

        } else {
            //throws exception  notRecognizedFilterException // TODO: define as database package exception
        }

        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                deliverableVo = new DeliverableVo(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getString(9),
                        cursor.getString(10),
                        cursor.getString(11),
                        cursor.getString(12),
                        cursor.getString(13),
                        cursor.getString(14)
                );

                // Adding deliverable to list
                deliverableVoList.add(deliverableVo);

            } while (cursor.moveToNext());
        }

        // return Initiative list
        return deliverableVoList;
    }

    /**
     * finishDeliverableById
     *
     * @param deliverableId
     */
    public void finishDeliverableById(String deliverableId) { //TODO: review to throw exception

        SQLiteDatabase db = this.databaseOpenHelper.getReadableDatabase();

        String sql = "UPDATE " + DatabaseOpenHelper.DATABASE_TABLE_DELIVERABLE + " SET "
                +  DatabaseOpenHelper.KEY_DELIVERABLE_status + "='FINISHED' "
                + " WHERE " + DatabaseOpenHelper.KEY_DELIVERABLE_iddeliverable + " = " + deliverableId;

        db.execSQL(sql); //use the method execSQL do modify the database
    }

    /**
     * deleteDeliverableById
     *
     * @param deliverableId
     */
    public void deleteDeliverableById(String deliverableId) { //TODO: review to throw exception

        SQLiteDatabase db = this.databaseOpenHelper.getReadableDatabase();

        String sql = "DELETE FROM " + DatabaseOpenHelper.DATABASE_TABLE_DELIVERABLE + " WHERE " + DatabaseOpenHelper.KEY_DELIVERABLE_iddeliverable + " = " + deliverableId;
        db.execSQL(sql); //use the method execSQL do modify the database
    }

    /**
     * updateDeliverablePriority
     *
     * @param deliverableId
     * @param prioritizedBy
     * @param priorityComment
     * @param isPriority
     */
    public void updateDeliverablePriority(String deliverableId, String prioritizedBy, String priorityComment, String isPriority) { //TODO: review to throw exception

        SQLiteDatabase db = this.databaseOpenHelper.getReadableDatabase();

        String sql = "UPDATE " + DatabaseOpenHelper.DATABASE_TABLE_DELIVERABLE + " SET "
                +  DatabaseOpenHelper.KEY_DELIVERABLE_prioritizedby + "='" + prioritizedBy + "', "
                +  DatabaseOpenHelper.KEY_DELIVERABLE_prioritycomment + "='" + priorityComment + "', "
                +  DatabaseOpenHelper.KEY_DELIVERABLE_ispriority + "='" + isPriority + "' "
                + " WHERE " + DatabaseOpenHelper.KEY_DELIVERABLE_iddeliverable + " = " + deliverableId;

        db.execSQL(sql); //use the method execSQL do modify the database
    }

    /**
     * updateDeliverable
     *
     * @param deliverableVo
     */
    public void updateDeliverable(DeliverableVo deliverableVo) { //TODO: review to throw exception

        SQLiteDatabase db = this.databaseOpenHelper.getReadableDatabase();

        String sql = "UPDATE " + DatabaseOpenHelper.DATABASE_TABLE_DELIVERABLE + " SET "
                +  DatabaseOpenHelper.KEY_DELIVERABLE_title + "='" + deliverableVo.getTitle() + "', "
                +  DatabaseOpenHelper.KEY_DELIVERABLE_description + "='" + deliverableVo.getDescription() + "', "
                +  DatabaseOpenHelper.KEY_DELIVERABLE_duedate + "='" + deliverableVo.getDuedate() + "' "
                + " WHERE " + DatabaseOpenHelper.KEY_DELIVERABLE_iddeliverable + " = " + deliverableVo.getIddeliverable();

        db.execSQL(sql); //use the method execSQL do modify the database
    }
}

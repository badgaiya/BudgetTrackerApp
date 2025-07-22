package com.example.budgettracker.database



import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) // Replace existing if ID conflicts (for ID=1)
    suspend fun insert(budget: Budget)

    @Update
    suspend fun update(budget: Budget)

    @Query("SELECT * FROM budget WHERE id = 1")
    fun getBudget(): Flow<Budget?> // Nullable as budget might not be set initially
}
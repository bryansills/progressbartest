package ninja.bryansills.progressbar.database

class RealUserQuerier(val appDatabase: AppDatabase) : UserQuerier {
    override suspend fun add(user: User) {
        appDatabase.userDao().insertAll(user)
    }
}

interface UserQuerier {
    suspend fun add(user: User)
}

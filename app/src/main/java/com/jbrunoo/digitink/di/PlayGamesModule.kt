package com.jbrunoo.digitink.di

import android.app.Activity
import android.content.Context
import com.google.android.gms.games.GamesSignInClient
import com.google.android.gms.games.LeaderboardsClient
import com.google.android.gms.games.PlayGames
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext

@Module
@InstallIn(ActivityComponent::class)
object PlayGamesModule {

    @Provides
    fun provideGameSignInClient(@ActivityContext context: Context): GamesSignInClient =
        PlayGames.getGamesSignInClient(context as Activity)

    @Provides
    fun provideLeaderBoardsClient(@ActivityContext context: Context): LeaderboardsClient =
        PlayGames.getLeaderboardsClient(context as Activity)
}

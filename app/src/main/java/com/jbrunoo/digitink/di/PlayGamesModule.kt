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
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
object PlayGamesModule {
    @Provides
    @ActivityScoped
    fun provideGameSignInClient(
        @ActivityContext context: Context,
    ): GamesSignInClient = PlayGames.getGamesSignInClient(context as Activity)

    @Provides
    @ActivityScoped
    fun provideLeaderBoardsClient(
        @ActivityContext context: Context,
    ): LeaderboardsClient = PlayGames.getLeaderboardsClient(context as Activity)
}

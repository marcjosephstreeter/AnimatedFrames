/*
 * Copyright 2015-2016 inventivetalent. All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification, are
 *  permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this list of
 *        conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice, this list
 *        of conditions and the following disclaimer in the documentation and/or other materials
 *        provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ''AS IS'' AND ANY EXPRESS OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *  ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  The views and conclusions contained in the software and documentation are those of the
 *  authors and contributors and should not be interpreted as representing official policies,
 *  either expressed or implied, of anybody else.
 */

package org.inventivetalent.animatedframes;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.inventivetalent.animatedframes.clickable.ClickListener;
import org.inventivetalent.animatedframes.metrics.Metrics;
import org.inventivetalent.pluginannotations.PluginAnnotations;
import org.inventivetalent.pluginannotations.config.ConfigValue;
import org.inventivetalent.update.spiget.SpigetUpdate;
import org.inventivetalent.update.spiget.UpdateCallback;
import org.inventivetalent.update.spiget.comparator.VersionComparator;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AnimatedFramesPlugin extends JavaPlugin {


	public static boolean ShowImgName = false;
	public FrameManager frameManager;
	public Executor     frameExecutor;

	public InteractListener interactListener;

	SpigetUpdate spigetUpdate;
	public boolean updateAvailable;

	@ConfigValue(path = "fixImageTypes")            boolean fixImageTypes           = false;
	@ConfigValue(path = "synchronizedStart") static boolean synchronizedStart       = false;
	@ConfigValue(path = "doNotStartAutomatically")  boolean doNotStartAutomatically = false;
	@ConfigValue(path = "maxAnimateDistance")       int     maxAnimateDistance      = 32;
	@ConfigValue(path = "defaultDelay")             int     defaultDelay            = 50;
	static                                          long    synchronizedTime        = 0;

	int maxAnimateDistanceSquared = 1024;

	@Override
	public void onEnable() {
		if (!Bukkit.getPluginManager().isPluginEnabled("MapManager")) {
			getLogger().warning("**************************************************");
			getLogger().warning("  ");
			getLogger().warning("         This plugin depends on MapManager        ");
			getLogger().warning("             https://r.spiget.org/19198            ");
			getLogger().warning("  ");
			getLogger().warning("**************************************************");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		saveDefaultConfig();
		PluginAnnotations.CONFIG.load(this, this);
		PluginAnnotations.COMMAND.load(this, new Commands(this));

		maxAnimateDistanceSquared = maxAnimateDistance * maxAnimateDistance;

		frameManager = new FrameManager(this);
		frameExecutor = Executors.newCachedThreadPool();

		getCommand("ToggleView").setExecutor(new ToggleViewEntityCommand());
		getCommand("Replace").setExecutor(new ReplaceCommand(this));
		Bukkit.getPluginManager().registerEvents(interactListener = new InteractListener(this), this);
		Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
		Bukkit.getPluginManager().registerEvents(new ClickListener(this), this);

		File cacheDir = new File(getDataFolder(), "cache");
		if (!cacheDir.exists()) { cacheDir.mkdirs(); }

		getLogger().fine("Waiting 2 seconds before loading data...");
		Bukkit.getScheduler().runTaskLaterAsynchronously(this, new Runnable() {
			@Override
			public void run() {
				getLogger().info("Loading data...");
				frameExecutor.execute(new Runnable() {
					@Override
					public void run() {
						frameManager.readFramesFromFile();
						getLogger().info("Loaded " + frameManager.size() + " frames.");
					}
				});
			}
		}, 40);

		new Metrics(this);

		spigetUpdate = new SpigetUpdate(this, 5583).setUserAgent("AnimatedFrames/" + getDescription().getVersion()).setVersionComparator(VersionComparator.SEM_VER_SNAPSHOT);
		spigetUpdate.checkForUpdate(new UpdateCallback() {
			@Override
			public void updateAvailable(String s, String s1, boolean b) {
				updateAvailable = true;
				getLogger().info("A new version is available (" + s + "). Download it from https://r.spiget.org/5583");
				//					getLogger().info("(If the above version is lower than the installed version, you are probably up-to-date)");
			}

			@Override
			public void upToDate() {
				getLogger().info("The plugin is up-to-date.");
			}
		});
	}

	@Override
	public void onDisable() {
		//		getLogger().info("Saving " + frameManager.size() + " frames...");
		//		frameExecutor.execute(new Runnable() {
		//			@Override
		//			public void run() {
		//		frameManager.writeFramesToFile();
		//		getLogger().info("Done.");
		//			}
		//		});
	}
}

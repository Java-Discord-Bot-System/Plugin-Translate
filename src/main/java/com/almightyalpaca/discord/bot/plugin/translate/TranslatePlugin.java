package com.almightyalpaca.discord.bot.plugin.translate;

import com.almightyalpaca.discord.bot.system.command.Command;
import com.almightyalpaca.discord.bot.system.command.CommandHandler;
import com.almightyalpaca.discord.bot.system.command.arguments.special.Rest;
import com.almightyalpaca.discord.bot.system.config.Config;
import com.almightyalpaca.discord.bot.system.events.commands.CommandEvent;
import com.almightyalpaca.discord.bot.system.exception.PluginLoadingException;
import com.almightyalpaca.discord.bot.system.exception.PluginUnloadingException;
import com.almightyalpaca.discord.bot.system.plugins.Plugin;
import com.almightyalpaca.discord.bot.system.plugins.PluginInfo;
import com.memetix.mst.MicrosoftTranslatorAPI;
import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

import net.dv8tion.jda.MessageBuilder;
import net.dv8tion.jda.MessageBuilder.Formatting;

public class TranslatePlugin extends Plugin {

	class TranslateCommand extends Command {

		public TranslateCommand() {
			super("translate", "Translate the given text.", "translate [to] [text]");
		}

		@CommandHandler(dm = true, guild = true, async = true)
		public void onCommand(final CommandEvent event, final String string) {
			if (string.equalsIgnoreCase("list")) {
				final MessageBuilder builder = new MessageBuilder();
				builder.appendString("Supported languages are:", Formatting.BOLD).newLine();
				for (final Language lang : Language.values()) {
					if (lang == Language.AUTO_DETECT) {
						continue;
					}
					try {
						builder.appendString(Language.ENGLISH.getName(lang)).newLine();
					} catch (final Exception e) {
						e.printStackTrace();
					}
				}
				event.sendMessage(builder);
			} else {
				this.onCommand(event, "en", new Rest(string));
			}
		}

		@CommandHandler(dm = true, guild = true, async = true)
		public void onCommand(final CommandEvent event, final String lang, final Rest text) {
			final MessageBuilder builder = new MessageBuilder();

			final Language to = Language.fromString(lang.toLowerCase().trim());
			if (to == null) {
				builder.appendString("Unknown language \"" + lang + "\"!", Formatting.BOLD);
			} else {
				try {
					final String translation = Translate.execute(text.getString(), to);
					builder.appendString("Translation:\n", Formatting.BOLD);
					builder.appendString(translation);
				} catch (final Exception e) {
					e.printStackTrace();
					builder.appendString("An unexpected error occured!", Formatting.BOLD);
				}
			}

			event.sendMessage(builder);
		}

	}

	private static final PluginInfo INFO = new PluginInfo("com.almightyalpaca.discord.bot.plugin.translate", "1.0.0", "Almighty Alpaca", "Translate Plugin", "Translate the given text.");

	public TranslatePlugin() {
		super(TranslatePlugin.INFO);
	}

	@Override
	public void load() throws PluginLoadingException {
		final Config config = this.getSharedConfig("microsoftazure");

		if (config.getString("id", "Your ID") == "Your ID" | config.getString("secret", "Your Secret") == "Your Secret") {
			throw new PluginLoadingException("Pls add your datamarket.azure.com id and secret to the config");
		}

		MicrosoftTranslatorAPI.setClientId(config.getString("id"));
		MicrosoftTranslatorAPI.setClientSecret(config.getString("secret"));

		preloadLanguageNames();

		this.registerCommand(new TranslateCommand());
	}

	private void preloadLanguageNames() {
		try {
			Language.ENGLISH.getName(Language.ENGLISH);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void unload() throws PluginUnloadingException {}

}

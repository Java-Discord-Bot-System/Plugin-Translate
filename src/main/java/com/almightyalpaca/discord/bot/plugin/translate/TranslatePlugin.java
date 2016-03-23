package com.almightyalpaca.discord.bot.plugin.translate;

import com.almightyalpaca.discord.bot.system.command.AbstractCommand;
import com.almightyalpaca.discord.bot.system.command.annotation.Command;
import com.almightyalpaca.discord.bot.system.command.arguments.special.Rest;
import com.almightyalpaca.discord.bot.system.config.Config;
import com.almightyalpaca.discord.bot.system.events.CommandEvent;
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

	class TranslateCommand extends AbstractCommand {

		public TranslateCommand() {
			super("translate", "Translate the given text.", "translate [to] [text]");
		}

		@Command(dm = true, guild = true, async = true)
		public void onCommand(final CommandEvent event, final String string) {
			if (string.equalsIgnoreCase("list")) {
				final MessageBuilder builder = new MessageBuilder();
				builder.appendString("Supported languages are:\n", Formatting.BOLD);
				for (final Language lang : Language.values()) {
					if (lang == Language.AUTO_DETECT) {
						continue;
					}
					try {
						builder.appendString(Language.ENGLISH.getName(lang));
					} catch (final Exception e) {
						e.printStackTrace();
					}
				}
				event.sendMessage(builder);
			} else {
				this.onCommand(event, "en", new Rest(string));
			}
		}

		@Command(dm = true, guild = true, async = true)
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

		final Config microsoftTranslateConfig = this.getBridge().getSecureConfig("MicrosoftTranslatorAPI");

		MicrosoftTranslatorAPI.setClientId(microsoftTranslateConfig.getString("ClientId"));
		MicrosoftTranslatorAPI.setClientSecret(microsoftTranslateConfig.getString("ClientSecret"));

		this.registerCommand(new TranslateCommand());

	}

	@Override
	public void unload() throws PluginUnloadingException {}

}

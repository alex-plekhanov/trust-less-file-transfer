package tlft;

import java.io.IOException;
import org.jline.reader.LineReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.shell.InputProvider;
import org.springframework.shell.Shell;
import org.springframework.shell.SpringShellAutoConfiguration;
import org.springframework.shell.jcommander.JCommanderParameterResolverAutoConfiguration;
import org.springframework.shell.jline.InteractiveShellApplicationRunner;
import org.springframework.shell.jline.JLineShellAutoConfiguration;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.shell.standard.StandardAPIAutoConfiguration;
import org.springframework.shell.standard.commands.StandardCommandsAutoConfiguration;

@Configuration
@Import({
    SpringShellAutoConfiguration.class,
    JLineShellAutoConfiguration.class,

    JCommanderParameterResolverAutoConfiguration.class,
    StandardAPIAutoConfiguration.class,

    StandardCommandsAutoConfiguration.class,
})
@ComponentScan(value = {"tlft"})
public class TlftShell {
    public static void main(String[] args) throws IOException {
        ApplicationContext context = new AnnotationConfigApplicationContext(TlftShell.class);
        Shell shell = context.getBean(Shell.class);
        shell.run(context.getBean(InputProvider.class));
    }

    @Bean
    @Autowired
    public InputProvider inputProvider(LineReader lineReader, PromptProvider promptProvider) {
        return new InteractiveShellApplicationRunner.JLineInputProvider(lineReader, promptProvider);
    }
}
